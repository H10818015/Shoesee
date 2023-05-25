import torch
from torchvision import transforms
from PIL import Image
from ResNet import resnet50
import matplotlib.pyplot as plt

#---------------------
#(0)參數設定
#---------------------     
#圖片路徑
img_path = ''
#權重參數路徑
weights_path = ''
#預測索引對應的類別名稱
class_names=['boots','high_heel','slippers','sports_shoes']

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
#(1)數據加載
#---------------------   
#預處理函數
data_transform = transforms.Compose([
    
    transforms.Resize((224,224)),
    transforms.ToTensor(),
    transforms.Normalize(mean=(0.5,0.5,0.5), std=(0.5,0.5,0.5))])

frame = Image.open(img_path)

plt.imshow(frame)
plt.title('')
plt.show()

img = data_transform(frame)
img = torch.unsqueeze(img, dim=0)

    
#---------------------
#(2)圖像預測
#---------------------   
#加載模型
model = resnet50(num_classes=4, include_top=True)
#加載權重文件
model.load_state_dict(torch.load(weights_path, map_location=device))

model.eval()

with torch.no_grad():
    
    outputs = model(img)
    
    outputs = torch.squeeze(outputs)
    
    predict = torch.softmax(outputs, dim=0)
    
    predict_cla = torch.argmax(predict).numpy()
    
    
predict_score = round(torch.max(predict).item(), 4)

predict_name = class_names[predict_cla]

plt.imshow(frame)
plt.title('class:'+str(predict_name)+'\n score:'+str(predict_score))
plt.show()