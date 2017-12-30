# STDAIAPI (For English version, please see below)
数据集与深度学习模型全生命周期管理平台

## 系统概述
### 概述
stdaiapi是数据集、深度学习模型全生命周期管理平台。包含数据集的建立、清洗、挖掘、共享，以及深度学习模型的建立、训练、部署、运行等托管服务。

系统以RESTful API方式提供服务，以及一个建立在API之上的命令行系统（ASH）提供更直观的交互。

### 架构
系统采取微服务架构，即将每个功能相对单一的模块作为一个最小运行单元。模块可独立运行、部署、横向扩展。提供一个更灵活、可扩展的服务架构。

### 模块划分
|模块|功能|状态|
|---|---|---|
|stdaiapi-algorithm|算法模块，算法的实现|
|stdaiapi-app|应用模块，应用级服务|未完成|
|stdaiapi-ash|ASH模块，提供一个命令行版操作系统|
|stdaiapi-biz|业务模块，提供系统正常运行所需业务功能|
|stdaiapi-core|核心模块，通用功能|
|stdaiapi-dao|DAO模块，提供关系型数据库访问接口|
|stdaiapi-data|数据模块，提供数据集相关功能|
|stdaiapi-distribute|分布式模块，提供系统分布式运行能力|未完成|
|stdaiapi-es|ES模块，提供ES数据库访问接口|
|stdaiapi-math|数学模块，提供数学函数|
|stdaiapi-ml|机器学习模块，封装机器学习算法，提供机器学习服务|
|stdaiapi-node|节点模块，提供节点间访问功能|未完成|
|stdaiapi-redis|REDIS模块，提供REDIS数据库访问接口|未完成|
|stdaiapi-statistic|统计模块，提供统计接口|

## 功能介绍
### 数据处理
系统提供一系列数据上传、标记、清洗、挖掘接口，其中数据挖掘算法如下

|类别|算法|接口|
|---|---|---|
|分类|KNN|/ml/classify|
|聚类|KMeans|/ml/cluster|
|决策树|C4.5|/ml/decision|

详细使用方法可见接口说明。另外，在stdaiapi-algorithm工程中有一些算法有待封装为接口。

### 深度学习
系统为深度学习模型提供建模、训练、部署、运行等全生命周期服务，其中深度学习模型如下

|类别|算法|接口|
|---|---|---|
|卷积神经网络|CNN|/ml/cnn|
|循环神经网络|LSTM|/ml/lstm|

详细使用方法可见接口说明。

### 业务功能
为保证业务正常运行，系统提供用户管理、TOKEN管理、数据字典管理等业务接口，详细使用方法可见接口说明。

### 调用入口
系统使用RESTful API提供服务，并在API基础上开发了网页版ASH命令行操作系统，图形化界面仍在计划中。

## 使用方法
### ASH
ASH是类Linux Shell的网页版命令行操作系统，用户可使用ASH操作数据集、模型等资源。ASH完全使用底层的RESTful API与服务器通信，像一个壳包裹住API，所以命名为Athvillar Shell，也即ASH名称的由来。ASH采用类Linux Shell的命令风格，如使用ls命令显示资源列表，cat命令查看资源，man命令获得命令帮助等。ASH本身带有完整的帮助系统和文档资源，可以使用help命令、man命令以及cat doc [文档名]命令获得详细的使用说明。

ASH的命令分为全局命令和资源命令两种，全局命令不针对某一类资源，使用格式为"命令名 [参数]"。全局命令包括

|命令|说明|状态|
|---|---|---|
|ash|解析ASH脚本，执行批处理任务|未完成|
|cd|在资源之间切换|
|curl|模拟shell的curl命令，发送http request|未完成|
|help|显示帮助信息|
|man|查看命令帮助|
|msg|收发系统内消息|
|login|用户登陆|
|logout|用户登出|
|version|查看版本信息|

资源命令必须针对某一类资源才有意义，使用格式为"命令名 [资源类别] [参数]"，如果省略资源类别，默认将使用当前目录作为资源类别。资源命令包括

|命令|说明|状态|
|---|---|---|
|call|调用资源，例如训练模型|
|cat|查看某资源详细信息，例如查看模型结构|
|cp|复制资源|未完成|
|find|查找资源|未完成|
|ls|资源列表，例如列出模型一览|
|mk|创建资源，例如建模|
|rm|删除资源|
|set|设置资源，例如为数据打标签|

平台包含的资源包括

|资源|说明|状态|
|---|---|---|
|data|数据，用户上传的数据，供模型训练和预测使用
|dic|数据字典，一般用作数据预处理
|doc|文档，包括平台使用说明和SDK
|file|用户脚本|未完成|
|filter|过滤器，数据预处理使用
|model|模型，用户建立的深度学习模型
|node|执行节点|未完成|
|user|用户|

可以使用"cd [资源类别]"命令在各资源间切换。可通过提示符查看切换是否成功，例如

    $model>

代表目前处于model资源目录下。

### RESTful API
对于非人工调用，例如程序间调用，应该使用SDK接口，本系统使用RESTful API作为标准SDK，包括

#### 业务模块
##### 用户相关接口
###### 增加用户接口
    curl -XPOST -H 'Content-Type: application/json' http://123.56.253.228/biz/user/xxx -d '{
        "password":"xxxxxx","email":"xxx@abc.com"
    }'

###### 查看用户接口（需要先调用获得TOKEN接口，下同）
    curl -XGET -H 'token: GHS1LOZWQK3Q25TTW' http://123.56.253.228/biz/user/xxx

###### 修改个人信息
    curl -XPOST -H 'token: 1WUJ1Q4IC7GRZEQT8' -H 'Content-Type: application/json' http://123.56.253.228/biz/user/xxx -d '{
      "password":"xxxxxx","email":"yyy@abc.com"
    }'

###### 修改密码
    curl -XPOST -H 'token: 1WUJ1Q4IC7GRZEQT8' -H 'Content-Type: application/json' http://123.56.253.228/biz/user/xxx -d '{
      "oldPassword":"xxxxxx","newPassword":"yyyyyy","email":"yyy@abc.com"
    }'

###### 删除用户接口
    curl -XDELETE -H 'token: ZEQT85A5UY3WLGPG8' http://123.56.253.228/biz/user/xxx

##### TOKEN相关接口
###### 用户登录，获得TOKEN
    curl -XPOST -H 'Content-Type: application/json' http://123.56.253.228/biz/token -d '{
        "userId": "xxx",
        "password":"xxxxxx"
    }'

###### 用户登出，删除TOKEN
    curl -XDELETE -H 'token: F5ECV3IHRIZQDGSV4' http://123.56.253.228/biz/token/F5ECV3IHRIZQDGSV4

##### MESSAGE相关接口
###### 发送消息
    curl -XPOST -H 'token: M56LGK18E7VN4Y6CF' -H 'Content-Type: application/json' http://123.56.253.228/biz/messages -d '{
        "toUserId": "xxx",
        "content": "hello"
    }'

###### 检索消息
    curl -XGET -H 'token: M56LGK18E7VN4Y6CF' 'http://123.56.253.228/biz/messages?type=receive&userId=xxx&all=1'

###### 删除消息
    curl -XDELETE -H 'token: M56LGK18E7VN4Y6CF' http://123.56.253.228/biz/messages

##### 数据集相关接口
###### 删除数据集
    curl -XDELETE http://123.56.253.228/biz/dataset/XLBER5WKJ3O97UKXKCAYONCV

#### 数据模块
##### 数据相关接口
###### 数据上传接口
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/data -d '
    {
        "dataName": "gender",
        "description": "gender dic",
        "sharePolicy": "protected",
        "format": "csv",
        "keywords": "k1,k2",
        "titles": "t1,t2",
        "data": [
            ["x1","y1"],
            ["x2","y2"]
        ]
    }'

###### 数据上传接口，只创建数据集
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/data -d '
    {
        "dataName": "yale",
        "description": "yale face",
        "sharePolicy": "protected",
        "type": "file",
        "format": "bmp",
        "keywords": "k1,k2",
        "titles": "t1,t2"
    }'

###### 上传本地文件至数据集
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI'  -F files=@'/Path-To-File/s1.bmp' -F files=@'/Path-To-File/s2.bmp' http://123.56.253.228/data/data/xxxUser/yale

###### 给数据集打标签
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/data/xxxUser/yale -d '
    {
      "updateBaseIdx": 1,
      "batchSet": {
        "label1": { "start": 1, "end": 11},
        "label2": { "start": 12, "end": 22}
      }
    }'

###### 上传数据至数据集并打标签
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/data/xxxUser/yaletest -d '
    {
      "updateBaseIdx": 1,
      "data": [
        ["x1","y1"],
        ["x2","y2"]
      ],
      "batchSet": {
        "label1": { "start": 1, "end": 11},
        "label2": { "start": 12, "end": 22}
      }
    }'

###### 获得数据集列表
    curl -XGET -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/data

###### 查看数据集
    curl -XGET -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/data/xxxUser/gender

###### 删除数据集
    curl -XDELETE -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/data/xxxUser/gender

##### 数据字典相关接口
###### 创建数据字典接口
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/dic -d '
    {
      "dicName": "gender",
      "description": "gender dic",
      "sharePolicy": "protected",
      "data": [
        {"key": 1, "value": "男"},
        {"key": 2, "value": "女"}
      ]
    }'

###### 查看数据字典列表接口
    curl -XGET -H 'token: NCZYJNUFN7NMTTTZC' http://123.56.253.228/data/dic

###### 查看数据字典接口
    curl -XGET -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/dic/xxxUser/gender

###### 删除数据字典接口
    curl -XDELETE -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/dic/xxxUser/gender

#### 数学模块
###### 随机数接口
    curl -XGET -H 'token: GHS1LOZWQK3Q25TTW' http://123.56.253.228/math/rand?num=1&len=3&letters=12345ABC

#### 机器学习模块
##### 分类
###### 通过datasetId
    curl -XPOST -H 'token: F5ECV3IHRIZQDGSV4' -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/classify -d '
    {
      "trainingSet":{
        "id": "V4Q1F7IWPI2GTZ9OK7GLQYQD"
      },
      "targetSet":{
        "data": [
          {"features":[90,92,22,73]},
          {"features":[58,33,33,44]}
        ]
      }
    }'

###### 通过datasetName
    curl -XPOST -H 'token: F5ECV3IHRIZQDGSV4' -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/classify -d '
    {
      "trainingSet":{
        "name": "testknn"
      },
      "targetSet":{
        "data": [
          {"features":[68,39,98,73]},
          {"features":[58,33,89,71]}
        ]
      }
    }'

###### 直接通过data
    curl -XPOST -H 'token: F5ECV3IHRIZQDGSV4' -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/classify -d '
    {
      "trainingSet":{
        "data": [
          {"features":[99,98,20,35], "category":"A"},
          {"features":[49,36,97,87], "category":"B"},
          {"features":[68,36,38,41], "category":"C"}
        ]
      },
      "targetSet":{
        "data": [
          {"features":[90,82,62,73]},
          {"features":[58,33,33,44]}
        ]
      }
    }'

##### 聚类
###### 聚类
    curl -XPOST -H 'token: F5ECV3IHRIZQDGSV4' -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/cluster -d '
    {
      "clusterNumber": 2,
      "trainingSet":{
        "data": [
          {"features":[99,98,20,35]},
          {"features":[49,36,97,87]},
          {"features":[68,36,38,41]},
          {"features":[58,42,74,39]},
          {"features":[99,99,17,10]},
          {"features":[91,89,37,52]},
          {"features":[97,96,26,39]},
          {"features":[70,87,98,96]},
          {"features":[71,62,99,95]},
          {"features":[70,62,47,38]},
          {"features":[68,39,98,73]},
          {"features":[76,55,47,63]}
        ]
      }
    }'

##### 决策树
###### 生成决策树
    curl -XPOST -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/decision -d '
    {
      "trainingSet":{
        "data": [
          "姓名,性别,名字几个字,80/90后,婚否,单身否",
          "X,D,D,D,D,D",
          "A,1,2,80,1,0",
          "B,0,2,80,1,0",
          "C,1,3,80,0,1",
          "D,0,3,90,0,0",
          "E,1,2,90,0,1",
          "F,0,2,80,0,1",
          "G,0,3,90,0,1",
          "H,1,3,90,0,0"
        ]
      }
    }'

##### 卷积神经网络（CNN）
###### 创建CNN
    curl -XPOST -H 'Content-Type: application/json' -H 'token: M56LGK18E7VN4Y6CF' http://123.56.253.228/ml/dnn -d '
    {
      "name": "testcnn",
      "algorithm": "cnn",
      "data": {
        "datasetId": "xxx",
        "datasetName": "xxx",
        "x": {
          "column": "table.data.ref",
          "filter": ["jpg2RGB2Double2"]
        },
        "y": {
            "colume": "table.data.y",
            "filter": "subString(1)|lookupDic2Integer(xxxx)" 
        }
      },
      "structure": {
        "layers" : [ 
            {"type": "INPUT", "width": 100, "height": 100, "depth": 1 },
            {"type": "POOL", "method": "max", "spatial": 2, "stride": 2},
            {"type": "CONV", "depth": 8, "stride": 1, "padding":1, "learningRate": 1, "aF": "sigmoid",
              "filter": {"width":3, "height":3} 
            },
            {"type": "POOL", "method": "max", "spatial": 2, "stride": 2},
            {"type": "CONV", "depth": 6, "stride": 1, "padding":1, "learningRate": 1, "aF": "sigmoid",
              "filter": {"width":3, "height":3} 
            },
            {"type": "FC", "depth": 15, "learningRate": 1, "aF": "sigmoid" } 
        ]
      }
    }'

##### 循环神经网络（LSTM）
###### 创建一个LSTM
    curl -XPOST -H 'Content-Type: application/json' -H 'token: WLJ5M83S6RJBYDUAS' http://123.56.253.228/ml/dnn -d '
    {
      "name": "testlstm",
      "algorithm": "lstm",
      "data": {
        "datasetId": "xxx",
        "datasetName": "xxx",
        "x": {
          "column": "table.data.x",
          "filter": ""
        },
        "y": {
            "colume": "table.data.y",
            "filter": "IntegerDicFilter(xxxUser/xxxDic)" 
        }
      },
      "structure": {
        "layerSize":[3,4],
        "inputSize":80,
        "outputSize":80,
        "delay": true
      }
    }'

###### 训练模型
    curl -XPOST -H 'Content-Type: application/json' -H 'token: WLJ5M83S6RJBYDUAS' http://123.56.253.228/ml/dnn/xxxUser/xxxModel -d '
    {
      "new": true,
      "train":{
        "diverseDataRate": [8,1,1],
        "dth":1,
        "learningRate":0.07,
        "epoch":8000,
        "trainSecond": 3600,
        "batchSize": 100,
        "watchEpoch":1,
        "testLossIncreaseTolerance":3
      }
    }'

###### 使用模型预测
    curl -XPOST -H 'Content-Type: application/json' -H 'token: WLJ5M83S6RJBYDUAS' http://123.56.253.228/ml/dnn/xxxUser/xxxModel/predict -d '
    {
        "lstm": {
          "terminator": ",",
          "steps":12
        }
        "data": {
          "datasetId": "xxx",
          "datasetName": "xxxUser/yale",
          "idx":[1,3,5],
          "x": {
            "column": "table.data.x",
            "filter": ""
          },
          "y": {
            "filter": "IntegerDicFilter(xxxUser/xxxDic)" 
          }
        },
      }
    }'

#### ASH模块
###### 执行命令
    curl -XPOST -H 'Content-Type: application/json' -H 'token: M56LGK18E7VN4Y6CF' http://123.56.253.228/ash/ash -d '
    {
      "ash": "ls -l",
      "resource": "model"
    }'

## 代码结构
### 分层结构
在不知道未来将有哪些扩展的状况下，尤其是人工智能领域发展如此之快，为了保持系统的可扩展性，设计为分层架构。这样做的目的是当某些功能、模块、甚至是层次不符合时代需要，需要被大规模修改乃至被完全替换的时候，系统的其它部分仍能够不受影响地继续工作下去。

|层次|职责|包含模块|发展方向|
|---|---|---|---|
|应用层|提供能解决某一行业问题的解决方案，如人脸识别、机器翻译等|stdaiapi-app|
|模型层|提供能解决某一类问题的通用模型，如模式识别、数据流预测等|stdaiapi-ml|
|算法层|提供数据挖掘、深度学习算法|stdaiapi-algorithm|算法创新|
|框架层|提供分布式计算框架、深度学习框架|stdaiapi-algorithm|支持GPU计算，Tensorflow等主流深度学习框架，MapReduce|
|数据层|提供数据访问接口，使数据存储位置、方式对上层透明|stdaiapi-dao, stdaiapi-es, stdaiapi-redis|

### 应用模块内分层
在某些面向前端的模块，如stdaiapi-ml、stdaiapi-biz等，使用SpringBoot框架，模块内部分为service层和agent层。service层面向前端调用，接收request，解析并分发给agent，将agent返回的结果包装为response返回给前端；agent负责业务逻辑处理，并在必要时继续向下调用底层模块。

## 部署方法
### 代码部署
#### 下载代码
git clone https://github.com/athvillar/stdaiapi

#### 编译
cd stdaiapi

maven build

#### 配置Nginx
配置nginx.conf中的server及upstream模块，注意端口应与application.properties中的server.port匹配。

    server {
        listen 80;
        server_name localhost,123.56.253.228;

        location / {
            root    /etc/nginx/html/;
            index   index.html;
        }

        location /math/v1/ {
            proxy_pass http://math-v1/math/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /ml/v1/ {
            proxy_pass http://ml-v1/ml/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /data/v1/ {
            proxy_pass http://data-v1/data/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /biz/v1/ {
            proxy_pass http://biz-v1/biz/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /ash/v1/ {
            proxy_pass http://ash-v1/ash/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /app/v1/ {
            proxy_pass http://app-v1/app/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /statistic/v1/ {
            proxy_pass http://statistic-v1/statistic/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }

    upstream math-v1 {
        server xxx.xxx.xxx.xxx:10101 weight=1;
    }
    upstream ml-v1 {
        server xxx.xxx.xxx.xxx:10102 weight=1;
    }
    upstream data-v1 {
        server xxx.xxx.xxx.xxx:10103 weight=1;
    }
    upstream biz-v1 {
        server xxx.xxx.xxx.xxx:10104 weight=1;
    }
    upstream ash-v1 {
        server xxx.xxx.xxx.xxx:10106 weight=1;
    }
    upstream app-v1 {
        server xxx.xxx.xxx.xxx:10107 weight=1;
    }
    upstream statistic-v1 {
        server xxx.xxx.xxx.xxx:10105 weight=1;
    }

#### 数据库
使用mysql数据库，执行stdaiapi/ddl.sql

#### 启动
分别启动各模块的main函数，包括

    cn.standardai.api.ash.Application
    cn.standardai.api.biz.Application
    cn.standardai.api.data.Application
    cn.standardai.api.math.Application
    cn.standardai.api.ml.Application

### docker部署
暂无镜像

## 改进方向
虽然现有版本是一个可执行版本，仍然有许多可改进的地方。现有版本不支持分布式计算、不支持GPU计算，可以通过在框架层引入Tensorflow解决这个问题，引入现有的深度学习框架来丰富深度学习平台。大数据处理方面，仍有许多可以改进的地方，可以通过引入Spark、HDFS来进一步增强系统的大数据处理能力。

现有的API、ASH入口不能满足初级用户需求，可以考虑开发一个图形化网页界面帮助用户了解系统功能。

对深度学习技术来说，最新的技术永远在实验室，论文会落后于实验室，深度学习框架会落后于论文，深度学习平台又会落后于深度学习框架，如此，系统将在算法的先进性上距离最新技术很远，应尽可能多实现一些最近的技术。

-------------------------

# STDAIAPI
A Lifecycle Management Platform for Deep Learning Model

## System Summary
### About System
Stdaiapi is a platform for managing deep learning model, including building, mining, sharing dataset, and for deep learning model, building, training, deploying and serving.

A RESTful API is provided, upon which a command system (ASH) is used for a more direct interacting.

### Architect
A micro-service architect is used. Every single functional module make a minimal unit for running, deploying and horizontal expansion. So it makes a more flexible and extendable service.

### Modules
|Module|Function|Status|
|---|---|---|
|stdaiapi-algorithm|Module for algorithm|
|stdaiapi-app|Module for application|unfinished|
|stdaiapi-ash|Module for athvillar shell, a command line OS for the platform|
|stdaiapi-biz|Module for platform business|
|stdaiapi-core|Common functions|
|stdaiapi-dao|Dao module for interface to DB|
|stdaiapi-data|Module for dataset functions|
|stdaiapi-distribute|Module to provide distribution capability|unfinished|
|stdaiapi-es|Elastic search DB access interface|
|stdaiapi-math|Math functions|
|stdaiapi-ml|Module providing machine learning services|
|stdaiapi-node|Distributed nodes module|unfinished|
|stdaiapi-redis|Redis DB access interface|unfinished|
|stdaiapi-statistic|Module providing statistic functions|

## Functions
### Data processing
Data uploading, tagging, mining interfaces are provided.

|Category|Algorithm|API|
|---|---|---|
|Classify|KNN|/ml/classify|
|Cluster|KMeans|/ml/cluster|
|Decision Tree|C4.5|/ml/decision|

For details please refer to API documents. Besides, there are also some algorithm not wrapped yet in stdaiapi-algorithm module.

### Deep Learning
Platform provide a lifecycle management for deep learning model. The followings are available algorithms.

|Category|Algorithm|API|
|---|---|---|
|CNN|CNN|/ml/cnn|
|RNN|LSTM|/ml/lstm|

For details please refer to API documents.

### Business functions
For business functions, user management, token management, dictionary functions are provided, for details please refer to API documents.

### Entrance
A RESTful API are provided for service, there is also a command line system called ASH. Graphic interface is under planning.

## Usage
### ASH
ASH is a shell-like command line system, user can use it to manipulate dataset, models and other types of resources. ASH uses RESTful API co access server, so it just like a shell, that is how the name Athvillar Shell(ASH) comes from. ASH also use a style like shell, "ls" for list, "cat" for check, "man" for manual, etc. ASH includes a whole help system and documents, user can use "help", "man" and "cat doc [document]" to obtain instructions in details.

There are 2 types of command in ASH, global command and resource command. Global command is not for resource, its format is "Command [Params]". Global command includes

|Command|Instruction|Status|
|---|---|---|
|ash|Resolve ASH scripts, run batch|unfinished|
|cd|Switch between resources|
|curl|Like curl in shell, send http request|unfinished|
|help|Show help information|
|man|Show manual of commnad|
|msg|Send & receive messages|
|login|Login to platform|
|logout|Logout|
|version|View version|

Resource command must be followed by a resource, its format is "Command [Resource Type] [Params]". If resource type is not specified, current resource is used as default. Resource command includes

|Command|Instruction|Status|
|---|---|---|
|call|Call resources, like training model|
|cat|View details of a resource, like inspect a model structure|
|cp|Copy resource|unfinished|
|find|Find resource|unfinished|
|ls|List resource, like list all models|
|mk|Create resource, like create a CNN model|
|rm|Delete resource|
|set|Set resource, like tagging a dataset|

Resource includes

|Resource|Instruction|Status|
|---|---|---|
|data|Data resource, upload by user, for training or predicting
|dic|Dictionary resource, for data pre-precessing
|doc|Document resource, including instructions for platform and SDK
|file|File resource like user scripts|unfinished|
|filter|Filter resource, for data pre-processing
|model|Model resource, created by user, mostly deep learning models
|node|Node resource, calculation unit|unfinished|
|user|User resource|

"cd" command is used for switch between resources.

    $model>

means model is current resource.

### RESTful API
For programming call, SDK are used. A RESTful API is our standard SDK, includes

#### Business Module Interface
##### User Related Interface
###### Add User
    curl -XPOST -H 'Content-Type: application/json' http://123.56.253.228/biz/user/xxx -d '{
        "password":"xxxxxx","email":"xxx@abc.com"
    }'

###### View User (you should get a token first, the same below)
    curl -XGET -H 'token: GHS1LOZWQK3Q25TTW' http://123.56.253.228/biz/user/xxx

###### Modify User
    curl -XPOST -H 'token: 1WUJ1Q4IC7GRZEQT8' -H 'Content-Type: application/json' http://123.56.253.228/biz/user/xxx -d '{
      "password":"xxxxxx","email":"yyy@abc.com"
    }'

###### Modify Password
    curl -XPOST -H 'token: 1WUJ1Q4IC7GRZEQT8' -H 'Content-Type: application/json' http://123.56.253.228/biz/user/xxx -d '{
      "oldPassword":"xxxxxx","newPassword":"yyyyyy","email":"yyy@abc.com"
    }'

###### Remove User
    curl -XDELETE -H 'token: ZEQT85A5UY3WLGPG8' http://123.56.253.228/biz/user/xxx

##### Token Related Interface
###### Get Token for Login
    curl -XPOST -H 'Content-Type: application/json' http://123.56.253.228/biz/token -d '{
        "userId": "xxx",
        "password":"xxxxxx"
    }'

###### Delete Token for Logout
    curl -XDELETE -H 'token: F5ECV3IHRIZQDGSV4' http://123.56.253.228/biz/token/F5ECV3IHRIZQDGSV4

##### Message Related Interface
###### Send Message
    curl -XPOST -H 'token: M56LGK18E7VN4Y6CF' -H 'Content-Type: application/json' http://123.56.253.228/biz/messages -d '{
        "toUserId": "xxx",
        "content": "hello"
    }'

###### Find Message
    curl -XGET -H 'token: M56LGK18E7VN4Y6CF' 'http://123.56.253.228/biz/messages?type=receive&userId=xxx&all=1'

###### Delete message
    curl -XDELETE -H 'token: M56LGK18E7VN4Y6CF' http://123.56.253.228/biz/messages

##### Dataset Related Interface
###### Delete Dataset
    curl -XDELETE http://123.56.253.228/biz/dataset/XLBER5WKJ3O97UKXKCAYONCV

#### Data Module Interface
##### Data Related Interface
###### Data Upload Interface
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/data -d '
    {
        "dataName": "gender",
        "description": "gender dic",
        "sharePolicy": "protected",
        "format": "csv",
        "keywords": "k1,k2",
        "titles": "t1,t2",
        "data": [
            ["x1","y1"],
            ["x2","y2"]
        ]
    }'

###### Data Upload Interface, create dataset only
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/data -d '
    {
        "dataName": "yale",
        "description": "yale face",
        "sharePolicy": "protected",
        "type": "file",
        "format": "bmp",
        "keywords": "k1,k2",
        "titles": "t1,t2"
    }'

###### Upload Local Files to Dataset
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI'  -F files=@'/Path-To-File/s1.bmp' -F files=@'/Path-To-File/s2.bmp' http://123.56.253.228/data/data/xxxUser/yale

###### Tagging Dataset
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/data/xxxUser/yale -d '
    {
      "updateBaseIdx": 1,
      "batchSet": {
        "label1": { "start": 1, "end": 11},
        "label2": { "start": 12, "end": 22}
      }
    }'

###### Upload Data & Tagging
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/data/xxxUser/yaletest -d '
    {
      "updateBaseIdx": 1,
      "data": [
        ["x1","y1"],
        ["x2","y2"]
      ],
      "batchSet": {
        "label1": { "start": 1, "end": 11},
        "label2": { "start": 12, "end": 22}
      }
    }'

###### List Dataset
    curl -XGET -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/data

###### View Dataset
    curl -XGET -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/data/xxxUser/gender

###### Remove Dataset
    curl -XDELETE -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/data/xxxUser/gender

##### Dictionary Related Interface
###### Create Dictionary
    curl -XPOST -H 'token: RSTR0X2BV3FH9BFSI' -H 'Content-Type: application/json' http://123.56.253.228/data/dic -d '
    {
      "dicName": "gender",
      "description": "gender dic",
      "sharePolicy": "protected",
      "data": [
        {"key": 1, "value": "男"},
        {"key": 2, "value": "女"}
      ]
    }'

###### List Dictionary
    curl -XGET -H 'token: NCZYJNUFN7NMTTTZC' http://123.56.253.228/data/dic

###### View Dictionary
    curl -XGET -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/dic/xxxUser/gender

###### Remove Dictionary
    curl -XDELETE -H 'token: RSTR0X2BV3FH9BFSI' http://123.56.253.228/data/dic/xxxUser/gender

#### Math Module Interface
###### Random String
    curl -XGET -H 'token: GHS1LOZWQK3Q25TTW' http://123.56.253.228/math/rand?num=1&len=3&letters=12345ABC

#### Machine Learning Module Interface
##### Classify Interface
###### By Dtaset ID
    curl -XPOST -H 'token: F5ECV3IHRIZQDGSV4' -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/classify -d '
    {
      "trainingSet":{
        "id": "V4Q1F7IWPI2GTZ9OK7GLQYQD"
      },
      "targetSet":{
        "data": [
          {"features":[90,92,22,73]},
          {"features":[58,33,33,44]}
        ]
      }
    }'

###### By Dataset Name
    curl -XPOST -H 'token: F5ECV3IHRIZQDGSV4' -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/classify -d '
    {
      "trainingSet":{
        "name": "testknn"
      },
      "targetSet":{
        "data": [
          {"features":[68,39,98,73]},
          {"features":[58,33,89,71]}
        ]
      }
    }'

###### By Data
    curl -XPOST -H 'token: F5ECV3IHRIZQDGSV4' -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/classify -d '
    {
      "trainingSet":{
        "data": [
          {"features":[99,98,20,35], "category":"A"},
          {"features":[49,36,97,87], "category":"B"},
          {"features":[68,36,38,41], "category":"C"}
        ]
      },
      "targetSet":{
        "data": [
          {"features":[90,82,62,73]},
          {"features":[58,33,33,44]}
        ]
      }
    }'

##### Cluster Interface
###### Cluster Interface
    curl -XPOST -H 'token: F5ECV3IHRIZQDGSV4' -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/cluster -d '
    {
      "clusterNumber": 2,
      "trainingSet":{
        "data": [
          {"features":[99,98,20,35]},
          {"features":[49,36,97,87]},
          {"features":[68,36,38,41]},
          {"features":[58,42,74,39]},
          {"features":[99,99,17,10]},
          {"features":[91,89,37,52]},
          {"features":[97,96,26,39]},
          {"features":[70,87,98,96]},
          {"features":[71,62,99,95]},
          {"features":[70,62,47,38]},
          {"features":[68,39,98,73]},
          {"features":[76,55,47,63]}
        ]
      }
    }'

##### Decision Tree Interface
###### Make Decision Tree
    curl -XPOST -H 'Content-Type: application/json' http://123.56.253.228/ml/dm/decision -d '
    {
      "trainingSet":{
        "data": [
          "NAME,GENDER,WORDS NUMBER IN NAME,80/90,MARRIED,SINGLE",
          "X,D,D,D,D,D",
          "A,1,2,80,1,0",
          "B,0,2,80,1,0",
          "C,1,3,80,0,1",
          "D,0,3,90,0,0",
          "E,1,2,90,0,1",
          "F,0,2,80,0,1",
          "G,0,3,90,0,1",
          "H,1,3,90,0,0"
        ]
      }
    }'

##### Convolution Neural Network
###### Create CNN
    curl -XPOST -H 'Content-Type: application/json' -H 'token: M56LGK18E7VN4Y6CF' http://123.56.253.228/ml/dnn -d '
    {
      "name": "testcnn",
      "algorithm": "cnn",
      "data": {
        "datasetId": "xxx",
        "datasetName": "xxx",
        "x": {
          "column": "table.data.ref",
          "filter": ["jpg2RGB2Double2"]
        },
        "y": {
            "colume": "table.data.y",
            "filter": "subString(1)|lookupDic2Integer(xxxx)" 
        }
      },
      "structure": {
        "layers" : [ 
            {"type": "INPUT", "width": 100, "height": 100, "depth": 1 },
            {"type": "POOL", "method": "max", "spatial": 2, "stride": 2},
            {"type": "CONV", "depth": 8, "stride": 1, "padding":1, "learningRate": 1, "aF": "sigmoid",
              "filter": {"width":3, "height":3} 
            },
            {"type": "POOL", "method": "max", "spatial": 2, "stride": 2},
            {"type": "CONV", "depth": 6, "stride": 1, "padding":1, "learningRate": 1, "aF": "sigmoid",
              "filter": {"width":3, "height":3} 
            },
            {"type": "FC", "depth": 15, "learningRate": 1, "aF": "sigmoid" } 
        ]
      }
    }'

##### Long Short Term Memory
###### Create LSTM
    curl -XPOST -H 'Content-Type: application/json' -H 'token: WLJ5M83S6RJBYDUAS' http://123.56.253.228/ml/dnn -d '
    {
      "name": "testlstm",
      "algorithm": "lstm",
      "data": {
        "datasetId": "xxx",
        "datasetName": "xxx",
        "x": {
          "column": "table.data.x",
          "filter": ""
        },
        "y": {
            "colume": "table.data.y",
            "filter": "IntegerDicFilter(xxxUser/xxxDic)" 
        }
      },
      "structure": {
        "layerSize":[3,4],
        "inputSize":80,
        "outputSize":80,
        "delay": true
      }
    }'

###### Train Model
    curl -XPOST -H 'Content-Type: application/json' -H 'token: WLJ5M83S6RJBYDUAS' http://123.56.253.228/ml/dnn/xxxUser/xxxModel -d '
    {
      "new": true,
      "train":{
        "diverseDataRate": [8,1,1],
        "dth":1,
        "learningRate":0.07,
        "epoch":8000,
        "trainSecond": 3600,
        "batchSize": 100,
        "watchEpoch":1,
        "testLossIncreaseTolerance":3
      }
    }'

###### Use Model to Predict
    curl -XPOST -H 'Content-Type: application/json' -H 'token: WLJ5M83S6RJBYDUAS' http://123.56.253.228/ml/dnn/xxxUser/xxxModel/predict -d '
    {
        "lstm": {
          "terminator": ",",
          "steps":12
        }
        "data": {
          "datasetId": "xxx",
          "datasetName": "xxxUser/yale",
          "idx":[1,3,5],
          "x": {
            "column": "table.data.x",
            "filter": ""
          },
          "y": {
            "filter": "IntegerDicFilter(xxxUser/xxxDic)" 
          }
        },
      }
    }'

#### ASH Module Interface
###### Execute Command
    curl -XPOST -H 'Content-Type: application/json' -H 'token: M56LGK18E7VN4Y6CF' http://123.56.253.228/ash/ash -d '
    {
      "ash": "ls -l",
      "resource": "model"
    }'

## Code Structure
### Layer Structure
AI tech grows so fast that we cannot know how far it will get. In order to keep maintain extendible, a multi-layer structure is used. Even some functions, modules become obsolete, other parts can still work well without effected.

|Layer|Responsibility|Modules|Work To Do|
|---|---|---|---|
|Application Layer|Provide solutions to a specific industry, e.g. face recognition|stdaiapi-app|
|Model Layer|Common module to resolve one kind of problems, e.g. pattern recognition|stdaiapi-ml|
|Algorithm Layer|Data mining, deep learning algorithm|stdaiapi-algorithm|Algorithm innovation|
|Framework Layer|Distributed calculation framework, deep learning framework|stdaiapi-algorithm|GPU, Tensorflow, MapReduce|
|Data Layer|Data access interface|stdaiapi-dao, stdaiapi-es, stdaiapi-redis|

### Layer inside module
Some of the modules, like stdaiapi-ml、stdaiapi-biz, use SpringBoot development framework. There is a service layer and an agent layer. Service layer receives request from frontend, dispatch it to agent layer, wraps the results from agent layer and send to frontend as response. Agent layer process logic part, sometimes calls other layers.

## Deployment
### By Code
#### Download Code
git clone https://github.com/athvillar/stdaiapi

#### Complie
cd stdaiapi

maven build

#### Config Nginx
Config server & upstream blocks in nginx.conf, note that the ports need to meet wich the server.port in application.properties.

    server {
        listen 80;
        server_name localhost,123.56.253.228;

        location / {
            root    /etc/nginx/html/;
            index   index.html;
        }

        location /math/v1/ {
            proxy_pass http://math-v1/math/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /ml/v1/ {
            proxy_pass http://ml-v1/ml/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /data/v1/ {
            proxy_pass http://data-v1/data/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /biz/v1/ {
            proxy_pass http://biz-v1/biz/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /ash/v1/ {
            proxy_pass http://ash-v1/ash/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /app/v1/ {
            proxy_pass http://app-v1/app/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /statistic/v1/ {
            proxy_pass http://statistic-v1/statistic/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }

    upstream math-v1 {
        server xxx.xxx.xxx.xxx:10101 weight=1;
    }
    upstream ml-v1 {
        server xxx.xxx.xxx.xxx:10102 weight=1;
    }
    upstream data-v1 {
        server xxx.xxx.xxx.xxx:10103 weight=1;
    }
    upstream biz-v1 {
        server xxx.xxx.xxx.xxx:10104 weight=1;
    }
    upstream ash-v1 {
        server xxx.xxx.xxx.xxx:10106 weight=1;
    }
    upstream app-v1 {
        server xxx.xxx.xxx.xxx:10107 weight=1;
    }
    upstream statistic-v1 {
        server xxx.xxx.xxx.xxx:10105 weight=1;
    }

#### Database
Mysql is used, to create tables, run stdaiapi/ddl.sql

#### Startup
Run main method in modules, includes

    cn.standardai.api.ash.Application
    cn.standardai.api.biz.Application
    cn.standardai.api.data.Application
    cn.standardai.api.math.Application
    cn.standardai.api.ml.Application

### By Docker
No images can be used yet

## Improvement
Now the project can work, although there is a lot to improve. Distributed calculation, GPU calculation are not supported yet, involving Tensorflow in framework layer can solve the question. Spark, HDFS should be involved also, to improve our capability of processing big data.

Entrance like API, ASH is a little difficulty for new users, we're consider to develop a graphic website to help people know/use our system.

The most up-to-date tech is in the library, then the papers, then framework like tensorflow. Our platform is far from the newest tech if we donot move on. We should make more algorithms, maybe a new framework.

