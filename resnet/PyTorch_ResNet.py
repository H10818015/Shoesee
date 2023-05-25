from __future__ import print_function, division
from PIL import ImageFile
ImageFile.LOAD_TRUNCATED_IMAGES = True
import torch
import torch.nn as nn
import torch.optim as optim
from torch.optim import lr_scheduler
import numpy as np
from torchvision import datasets, models, transforms
import matplotlib.pyplot as plt
import torchmetrics
from torchmetrics import Accuracy
from pytorchtools import EarlyStopping
from sklearn.metrics import confusion_matrix
import seaborn as sns
import pandas as pd
import time
import os
from PIL import Image

os.environ["KMP_DUPLICATE_LIB_OK"]="TRUE"
import copy

EPOCH_VALUE = 350
PREDICTED_IMAGE_NUM = 200    
isShowPredictedPic = True    
BATCH_SIZE = 16        
best_train_acc = 0.0 
best_val_acc = 0.0 


data_transforms = {
    'train': transforms.Compose([
        transforms.RandomResizedCrop(256), 
        # transforms.Resize(256),  
        # transforms.CenterCrop(256), 
        transforms.RandomHorizontalFlip(), 
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]) 
    ]),
    'val': transforms.Compose([
        transforms.Resize(256), 
        transforms.CenterCrop(256),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]) 
    ]),    
    'test': transforms.Compose([
        transforms.Resize(256), 
        transforms.CenterCrop(256),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]) 
    ]),
}
data_dir = 'D:/1shoes/Resnet 50/'

image_datasets = {x: datasets.ImageFolder(os.path.join(data_dir, x), data_transforms[x])
                  for x in ['train', 'val', 'test']}
print()
print('Batch Size: ', BATCH_SIZE)
dataloaders = {x: torch.utils.data.DataLoader(image_datasets[x], batch_size=BATCH_SIZE,
               shuffle=True, num_workers=0)
               for x in ['train', 'val']}                                                
dataloaders_test = {x: torch.utils.data.DataLoader(image_datasets[x], batch_size=4,
               shuffle=True, num_workers=0)
               for x in ['test']}



dataset_sizes = {x: len(image_datasets[x]) for x in ['train', 'val', 'test']}
print()
print('Dataset Size: ', dataset_sizes)
print()
class_names = image_datasets['train'].classes
print('Class Name: ', class_names)
print()
device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
if torch.cuda.is_available():
    print('CUDA is available. Training on GPU ...')
else:
    print('CUDA is not available. Training on CPU ...')    
print()
print('=' * 45)
print()

def tensor2img(inp):
    inp = inp.numpy().transpose((1, 2, 0))
    mean = np.array([0.485, 0.456, 0.406])
    std = np.array([0.229, 0.224, 0.225])
    inp = std * inp + mean
    inp = np.clip(inp, 0, 1)
    return inp
  
def train_model(model, criterion, optimizer, scheduler, num_epochs):
    total_since = time.time()     
    best_model_wts = copy.deepcopy(model.state_dict())
    global best_train_acc 
    global best_val_acc     
    train_loss = [0] * num_epochs
    train_acc = [0] * num_epochs
    val_loss = [0] * num_epochs
    val_acc = [0] * num_epochs     
    for epoch in range(num_epochs):
        print()
        print('-' * 30)
        print('Epoch {}/{}'.format(epoch+1, num_epochs))
        print()        
        lr = optimizer.param_groups[0]['lr']
        print(f'LR: {lr:.7f}', end=' ')
        print()
        print()        
        epoch_since = time.time()         
        for phase in ['train', 'val']:
            if phase == 'train':
                model.train()  
            else:
                model.eval()   
            running_loss = 0.0
            running_corrects = 0
            for inputs, labels in dataloaders[phase]:
                inputs = inputs.to(device)
                labels = labels.to(device)
                optimizer.zero_grad(set_to_none=True) 
                with torch.set_grad_enabled(phase == 'train'):
                    outputs = model(inputs)             
                    _, preds = torch.max(outputs, 1) 
                    loss = criterion(outputs, labels) 
                    if phase == 'train':
                        loss.backward()  
                        optimizer.step() 
                running_loss += loss.item() * inputs.size(0)
                running_corrects += torch.sum(preds == labels.data)
            if phase == 'train':
                scheduler.step()
            epoch_loss = running_loss / dataset_sizes[phase]
            epoch_acc = running_corrects.double() / dataset_sizes[phase]           
            if phase == 'train':
                train_loss[epoch] = epoch_loss
                train_acc[epoch] = epoch_acc
                if epoch_acc > best_train_acc:
                    best_train_acc = epoch_acc
                    best_train_epoch = epoch +1
            else:
                val_loss[epoch] = epoch_loss
                val_acc[epoch] = epoch_acc
            if phase == 'val' and epoch_acc > best_val_acc:
                best_val_acc = epoch_acc
                best_model_wts = copy.deepcopy(model.state_dict())  
                best_val_epoch = epoch +1

        print()        
        print('Training Loss: {:.4f} Acc: {:.4f}'.format(
            train_loss[epoch], train_acc[epoch]))
        print()
        print('Validation Loss: {:.4f} Acc: {:.4f}'.format(
            val_loss[epoch], val_acc[epoch]))
        print()       
        time_elapsed = time.time() - epoch_since       
        print('Training time of this epoch: {:.0f}m {:.0f}s'.format(
            time_elapsed // 60, time_elapsed % 60))    
        print()
    time_elapsed = time.time() - total_since
    print()
    print('-' * 30)
    print('The whole training time: {:.0f}m {:.0f}s'.format(
        time_elapsed // 60, time_elapsed % 60))
    print()    
    print('Best Train Epoch:',best_train_epoch)
    print()
    print('Best Train Acc: {:4f}'.format(best_train_acc))
    print()   
    print('Best Validation Epoch:',best_val_epoch)
    print()
    print('Best Validation Acc: {:4f}'.format(best_val_acc))
    print()
    print()

    fig_loss = plt.figure(figsize=(36, 24))      
    plt.title('Train/Validation loss', fontsize=45)
    plt.ylabel('Loss', fontsize=30)
    plt.xlabel('Epochs', fontsize=30)
    plt.legend(['Train Loss', 'Validation Loss'], loc='upper right', fontsize=30)  
    x = range(1,EPOCH_VALUE+1)
    plt.xticks(x, fontsize=26, rotation='vertical')     
    plt.yticks(fontsize=26)     
    plt.plot(x, torch.tensor(train_loss, device='cpu'))
    plt.plot(x, torch.tensor(val_loss, device='cpu'))
    save_filename = "img_"+time.strftime("%Y%m%d-%H%M%S")
    plt.savefig("D:/1shoes/Resnet 50/chart/"+save_filename+"_loss.png") 
    plt.show()    
    fig_acc = plt.figure(figsize=(36, 24))    
    plt.title('Train/Validation accuracy', fontsize=45)
    plt.ylabel('Accuracy', fontsize=30)
    plt.xlabel('Epochs', fontsize=30)
    plt.legend(['Train Accuracy', 'Validation Accuracy'], loc='lower right', fontsize=30)
    x = range(1,EPOCH_VALUE+1)
    plt.xticks(x, fontsize=26, rotation='vertical')    
    plt.yticks(fontsize=26)     
    plt.plot(x, torch.tensor(train_acc, device='cpu'))
    plt.plot(x, torch.tensor(val_acc, device='cpu'))
    save_filename = "img_"+time.strftime("%Y%m%d-%H%M%S")
    plt.savefig("D:/1shoes/Resnet 50/chart/"+save_filename+"_accuracy.png")
    plt.show()
    # fig_loss_acc = plt.figure(figsize=(36, 24))    
    # plt.title('Train/Validation loss & accuracy', fontsize=45)
    # plt.ylabel('Loss / Accuracy', fontsize=30)
    # plt.xlabel('Epochs', fontsize=30)
    # plt.legend(['Train Loss', 'Validation Loss', 'Train Accuracy', 'Validation Accuracy'], bbox_to_anchor=(0.875, 1.1), ncol=4, fontsize=30)
    # x = range(1,EPOCH_VALUE+1)
    # plt.xticks(x, fontsize=26, rotation='vertical')   
    # plt.yticks(fontsize=26)     
    # plt.plot(x, torch.tensor(train_loss, device='cpu'))
    # plt.plot(x, torch.tensor(val_loss, device='cpu'))
    # plt.plot(x, torch.tensor(train_acc, device='cpu'))
    # plt.plot(x, torch.tensor(val_acc, device='cpu'))
    # save_filename = "img_"+time.strftime("%Y%m%d-%H%M%S")
    # plt.savefig("D:/1shoes/Resnet 50/chart/"+save_filename+"_loss_acc.png")       
    # plt.show()
    model.load_state_dict(best_model_wts)
    return model

    def __call__(self, val_loss, model):

        score = -val_loss

        if self.best_score is None:
            self.best_score = score
            self.save_checkpoint(val_loss, model)
        elif score < self.best_score + self.delta:
            self.counter += 1
            self.trace_func(f'EarlyStopping counter: {self.counter} out of {self.patience}')
            if self.counter >= self.patience:
                self.early_stop = True
        else:
            self.best_score = score
            self.save_checkpoint(val_loss, model)
            self.counter = 0

    def save_checkpoint(self, val_loss, model):
        '''Saves model when validation loss decrease.'''
        if self.verbose:
            self.trace_func(f'Validation loss decreased ({self.val_loss_min:.6f} --> {val_loss:.6f}).  Saving model ...')
        torch.save(model.state_dict(), self.path)
        self.val_loss_min = val_loss
    
def visualize_model(model, num_images=PREDICTED_IMAGE_NUM):
    was_training = model.training 
    model.eval() 
    images_so_far = 0                   
    with torch.no_grad(): 
        for i, (inputs, labels) in enumerate(dataloaders_test['test']):            
            if isShowPredictedPic == True:
                fig = plt.figure(i)             
            inputs = inputs.to(device)
            labels = labels.to(device)      
            outputs = model(inputs)
            _, preds = torch.max(outputs, 1)                            
            for j in range(inputs.size()[0]):             
                images_so_far += 1  
                j += 1                 
                if isShowPredictedPic == True:                    
                    ax = plt.subplot(2, 2, j)  
                    ax.axis('off')                      
                    j -= 1
                    ax.set_title('predicted: {}'.format(class_names[preds[j]]), fontsize=10)  
                    img = tensor2img(inputs.cpu().data[j])                                          
                    j += 1             
                    plt.imshow(img)
                    time.sleep(1)                                              
                if images_so_far >= num_images:
                    model.train(mode=was_training)                     
                    if isShowPredictedPic == True:
                        save_filename = "img_"+time.strftime("%Y%m%d-%H%M%S")
                        fig = plt.gcf() 
                        plt.savefig("D:/1shoes/Resnet 50/predicted/"+save_filename+".png")
                        plt.show()
                        model.train(mode=was_training) 
                    return             
            if isShowPredictedPic == True:                                                                                      
                save_filename = "img_"+time.strftime("%Y%m%d-%H%M%S")
                fig = plt.gcf() 
                plt.savefig("D:/1shoes/Resnet 50/predicted/"+save_filename+".png")
                plt.show()    

            y_true = []
            y_pred = []

            for i, (inputs, labels) in enumerate(dataloaders_test['test']):
                inputs = inputs.to(device)
                labels = labels.to(device)

                with torch.no_grad():
                    outputs = model(inputs)
                    _, preds = torch.max(outputs, 1)

                for j in range(inputs.size()[0]):
                    y_true.append(labels[j].cpu().numpy())
                    y_pred.append(preds[j].cpu().numpy())

            cnf_matrix = confusion_matrix(y_true, y_pred)
            df_cm = pd.DataFrame(cnf_matrix, index=class_names, columns=class_names)
            plt.figure(figsize=(5,3))
            sns.heatmap(df_cm, annot=True, fmt='d')
            plt.savefig("D:/1shoes/Resnet 50/confusion_matrix/"+save_filename+".png")
            plt.show()

                                                                                                                                                                                                   
if __name__ == '__main__':    
    print()
    print('Using Transfer Learning 之 finetuning the convnet(微調卷積網路-ft)訓練方式.')
    print()    
    if os.path.isfile('result/trained_model/ResNet-152.pth'):  
        model_ft = torch.load('result/trained_model/ResNet-50.pth')
        print('Load self-trained ResNet-50 model.')
        print()
        
        # model_ft = torch.load('result/trained_model/ResNet-152.pth')
        # print('Load self-trained ResNet-152 model.')
        # print()
    else:
        model_ft = models.resnet50(weights='ResNet50_Weights.DEFAULT')
        print()
        print('Load default pretrained ResNet-50 model.')
        print()
        
        # model_ft = models.resnet152(weights='ResNet152_Weights.DEFAULT')
        # print()
        # print('Load default pretrained ResNet-152 model.')
        # print()    
    
        
    num_ftrs = model_ft.fc.in_features    
    model_ft.fc = nn.Linear(num_ftrs, len(class_names))
    model_ft = model_ft.to(device)
    criterion = nn.CrossEntropyLoss()
    optimizer_ft = optim.SGD(model_ft.parameters(), lr=0.0001, momentum=0.9)
    earlystop = EarlyStopping(patience = 10, verbose=True)
    exp_lr_scheduler = lr_scheduler.StepLR(optimizer_ft, step_size=70, gamma=0.5)
    model_ft = train_model(model_ft, criterion, optimizer_ft, 
                           exp_lr_scheduler, num_epochs=EPOCH_VALUE)
    save_filename = "D:/1shoes/Resnet 50/trained_model/"+time.strftime("%Y%m%d-%H%M%S-")+"ResNet50-ft-"+'({:4f}_{:4f})-'.format(best_train_acc, best_val_acc)+'({})-'.format(BATCH_SIZE)+'({})'.format(EPOCH_VALUE)+".pth"
    # save_filename = "D:/1shoes/Resnet 50/trained_model/"+time.strftime("%Y%m%d-%H%M%S-")+"ResNet152-ft-"+'({:4f}_{:4f})-'.format(best_train_acc, best_val_acc)+'({})-'.format(BATCH_SIZE)+'({})'.format(EPOCH_VALUE)+".pth"    
    torch.save(model_ft, save_filename) 
    visualize_model(model_ft)
