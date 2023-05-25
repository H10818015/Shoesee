import torch
from torch import nn, optim
from torchvision import datasets, transforms
from torch.utils.data import DataLoader
from ResNet import resnet50
import numpy as np
import matplotlib.pyplot as plt
from keras.callbacks import EarlyStopping

#參數設置

batch_size = 16
epochs = 2

#文件配置

#數據集位置
filepatch = 'D:/1shoes/Resnet 50/'

#權重文件
weightpath = 'D:/1shoes/Resnet 50/predtrained/resnet50.pth'

#權重文件保存資料夾路徑
savepatch = 'D:/1shoes/Resnet 50/save'

# if torch.cuda.is_available():
#     device = torch.aevice('cuda:0')
# else:
#     device = torch.aevice('cpu')    
    

if torch.cuda.is_available():
    device = torch.device("cuda:0")
    print("running on the GPU")
else:
    device = torch.device("cpu")
    print("running on the CPU")

#---------------------
#(2)架構數據集
#---------------------
#訓練集的數據預處理
transform_train = transforms.Compose([
    transforms.RandomResizedCrop(224),
    transforms.RandomHorizontalFlip(),
    transforms.ToTensor(),
    transforms.Normalize(mean=(0.5,0.5,0.5), std=(0.5,0.5,0.5))])

#驗證集的數據預處理
transform_val = transforms.Compose([
    transforms.Resize((224,224)),
    transforms.ToTensor(),
    transforms.Normalize(mean=(0.5,0.5,0.5), std=(0.5,0.5,0.5))])

#讀取訓練集並預處理
train_dataset = datasets.ImageFolder(root=filepatch + 'train',
                                      transform = transform_train)
#讀取驗證集並預處理
val_dataset = datasets.ImageFolder(root=filepatch + 'val',
                                      transform = transform_val)

#查看訓練集及驗證集的量
train_num = len(train_dataset)
val_num = len(val_dataset)
print('train_num:', train_num, 'val_num:', val_num)

#查看圖像類別及對應索引
class_dict = train_dataset.class_to_idx
print(class_dict)
#將類別名稱保存在列表中 #{'boots':0, 'high_heel':1, 'slippers':2, 'sports_shoes:3}
class_names = list(class_dict.keys())

EarlyStopping(
    monitor='val_loss', min_delta=0, 
    patience=10, verbose=0, mode='auto',
    baseline=None, restore_best_weights=False
)


#架構訓練集
train_loader = DataLoader(dataset=train_dataset,
                          batch_size=batch_size,
                          shuffle=True,
                          num_workers=0)

#架構驗證集
val_loader = DataLoader(dataset=val_dataset,
                        batch_size=batch_size,
                        shuffle=False,
                        num_workers=0)

#---------------------
#(3)數據可視化
#---------------------

#取出一個batch訓練集，返回圖片及標籤
train_img, train_label = iter(train_loader).__next__()
#查看shape,img=[32.3.224.224], label=[32]
print(train_img.shape, train_label.shape)

img = train_img[:9]
img = img / 2 + 0.5

img = img.numpy()
class_label = train_label.numpy()

img = np.transpose(img, [0,2,3,1])

plt.figure()

for i in range(img.shape[0]):
    plt.subplot(3, 3, i+1)
    plt.imshow(img[i])
    plt.xticks([])
    plt.yticks([])
    plt.title(class_names[class_label[i]])
    
plt.tight_layout()
plt.show()

#---------------------
#(4)加載模型
#---------------------

net = resnet50(num_classes=1000, include_top=True)


net.load_state_dict(torch.load(weightpath, map_location=device))
    

in_channel = net.fc.in_features
net.fc = nn.Linear(in_channel,4)

net.to(device)
loss_function = nn.CrossEntropyLoss()
optimizer = optim.Adam(net.parameters(), lr=0.002)

best_acc = 0.0

#---------------------
#(5)網路訓練
#---------------------
for epoch in range(epochs):
    
    print('-'*30, '\n','epoch:', epoch)
    
    net.train()
    
    running_loss = 0.0
    
    for step, data in enumerate(train_loader):
        
        images, labels =data
        
        optimizer.zero_grad()
        
        outputs = net(images.to(device))
        
        loss = loss_function(outputs, labels.to(device))
        
        loss.backward()
        
        optimizer.step()
        
        running_loss += loss.item()
        
        print(f'step:{step} loss:{loss}')
        
#---------------------
#(6)網路驗證
#---------------------     

    net.eval()
    acc = 0.0
    with torch.no_grad():
        
        for data_test in val_loader:
            
            test_images, test_labels = data_test
            
            outputs = net(test_images.to(device))
            
            predict_y = torch.max(outputs, dim=1)[1]
            
            acc += (predict_y == test_labels.to(device)).sum().item()
            
        acc_test = acc / val_num
        
        print (f'total_train_loss:{running_loss/step}, total_test_acc:{acc_test}')
        
        
#---------------------
#(7)權重保存
#---------------------
        if acc_test > best_acc:
            
            best_acc = acc_test
            
            savename = savepath = 'resnet51.pth'
            
            torch.save(net.state_dict(), savename)
            
plt.figure(figsize = (15,5))
plt.subplot(1,2,1)
plt.plot(train_loader['accuracy'])
plt.plot(val_loader['val_accuracy'])
plt.title('model accuracy')
plt.ylabel('accuracy')
plt.xlabel('epoch')

plt.legend(['train','test'], loc = 'upper left')

plt.subplot(1,2,2)
plt.plot(train_loader['loss'])
plt.plot(val_loader['val_loss'])
plt.title('model loss')
plt.ylabel('loss')
plt.xlabel('epoch')
plt.legend(['train','test'],loc = 'upper left')
plt.show()
plt.show()
