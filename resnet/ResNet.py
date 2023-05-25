import torch
from torch import nn
from torchstat import stat     #查看網路參數
from torchsummary import summary #查看網路結構





#---------------------
#(1)殘差單元
# x-->捲積-->bn-->relu-->捲積-->be-->輸出
#---------------------identity(短接)

# ...
# in_channel
# out_channel
# stride=1
# downsample
# ...
#---------------------

class Bottleneck(nn.Module):
    #最後一個1*1卷積下降的通道
    expansion = 4
    
    #初始化
    def __init__(self,in_channel ,out_channel, stride=1, downsample=None):
        #繼承父類初始化方式
        super(Bottleneck,self).__init__()
        #屬性分配
                       
        self.conv1 = nn.Conv2d(in_channels=in_channel, out_channels=out_channel,
                               kernel_size=1, stride=1, padding=0, bias=False)
        
        self.bn1 = nn.BatchNorm2d(out_channel)
        
        self.relu = nn.ReLU(inplace=True)
        
        self.conv2 = nn.Conv2d(in_channels=out_channel,out_channels=out_channel,
                               kernel_size=3, stride=stride, padding=1, bias=False)
        
        self.bn2 = nn.BatchNorm2d(out_channel)
        
        self.conv3 = nn.Conv2d(in_channels=out_channel,out_channels=out_channel*self.expansion,
                               kernel_size=1, stride=1, padding=0, bias=False)
        
        self.bn3 = nn.BatchNorm2d(out_channel*self.expansion)
        
        self.downsample = downsample
        
    
    def foeward(self, x):
        
        identity = x
        
        if self.downsample is not None :
            
            identity = self.downsample(x)
            
        x = self.conv1(x)
        x = self.bn1(x)
        x = self.relu(x)
        
        x = self.conv2(x)
        x = self.bn2(x)
        x = self.relu(x)
        
        x = self.conv3(x)
        x = self.bn3(x)
             
        x = x + identity
        
        x = self.relu(x)
        
        return x  #輸出殘差單元的結果
    
#---------------------
#(2)網路構建
# ...
# block: 殘差單元
# blocks_num:每個殘差結構使用殘差單元數量
# num_classes :分類數量
# include_top: 是否包含分類層(全連接)
# ...
#---------------------

class ResNet(nn.Module):
    
    def __init__(self,block, blocks_num, num_classes=1000, include_top=True):
        
        super(ResNet, self).__init__()
        
        self.include_top = include_top
        self.in_channel = 64
        
        self.conv1 = nn.Conv2d(in_channels=3, out_channels=self.in_channel,
                               kernel_size=7, stride=2, padding=3, bias=False)
        
        self.bn1 = nn.BatchNorm2d(self.in_channel)
        
        self.relu = nn.ReLU(inplace=True)
        
        self.maxpool = nn.MaxPool2d(kernel_size=3, stride=2, padding=1)
        
        self.layer1 = self._make_layer(block, 64, blocks_num[0])
        
        self.layer2 = self._make_layer(block, 128, blocks_num[1], stride=2)
        self.layer3 = self._make_layer(block, 256, blocks_num[2], stride=2)
        self.layer4 = self._make_layer(block, 512, blocks_num[3], stride=2)
        
        
        if self.include_top:
            
            self.avgpool= nn.AdaptiveAvgPool2d((1,1))
            
            self.fc = nn.Linear(512*block.expansion, num_classes)
            
        for m in self.modules():
            if isinstance(m, nn.Conv2d):
                nn.init.kaiming_normal_(m.weight, mode='fan_out')
                
        
    # 殘差結構  
    # ...
    # block: 殘差單元
    # channel:殘差結構中第一個卷積層的輸出通道數
    # blocks_num:代表一個殘差結構包含多少個殘差單元
    # stride: 是否下採樣stride=2
    # ...
    
    
    def _make_layer(self, block, channel, block_num, stride=1):
        
        downsample = None
        
        if stride != 1 or self.in_channel !=channel * block.expansion:
            
            downsample = nn.Sequential(
                nn.Conv2d(in_channels=self.in_channel, out_channels=channel*block.expansion,
                                       kernel_size=1, stride=stride, bias=False),
                nn.BatchNorm2d(channel*block.expansion))
            
        layers = []
        
        layers.append(block(self.in_channel, channel, stride=stride, downsample=downsample))
        
        self.in_channel = channel*block.expansion
        
        for _ in range(1, block_num):
            layers.append(block(self.in_channel, channel))
            
        return nn.Sequential(*layers)
    
    def forward(self, x):
        
        x = self.conv1(x)
        x = self.bn1(x)
        x = self.relu(x)
        x = self.maxpool(x)
        
        x = self.layer1(x)
        x = self.layer2(x)
        x = self.layer3(x)
        x = self.layer4(x)
        
        if self.include_top:
            
            x = self.avgpool(x)
            
            x = torch.flatten(x,1)
            
            x =  self.fc(x)
        

        return x
    
#構建resnet50
def resnet50(num_classes=1000, include_top=True):
    return ResNet(Bottleneck, [3,4,6,3], num_classes=num_classes, include_top=include_top)
    
if __name__ == '__main__':
        model = resnet50()
                
        stat(model, input_size=(3,224,224))
                
        summary(model, input_size=[(3,224,224)], device='cpu')