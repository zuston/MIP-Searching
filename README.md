# material-platform
![Build Status](https://api.travis-ci.org/zuston/MIP-Searching.svg?branch=master)

### 项目采用内网连接数据库方式
### 直接新建 /opt/mongo.properties 文件 
host=192.168.1.1  
port=27147  
dbName=material  
username=root  
pwd=1qaz2wsx3edc  

### mvn clean package 即可打包部署

***

### WorkFlow DataFormat
[workFlow wiki](https://github.com/zuston/MIP-Searching/wiki/WorkFlow-DataFormat-Design)  

[workFlow file](https://github.com/zuston/MIP-Searching/blob/master/src/main/java/workflow/workflow.json)

###  searching formual
* **_1A&(H|2A)_**
* K&S&(sg=216)&(es=1:1:1*)&(ve=8|12)&1A&(en=3|8)^
* ^ 为只包含的 tag, * 为 ratio 中比例全排列的 tag
* 同时在子查询中支持 **_sg=10-20_** or **_sg>=10_** or **_sg<30_** or **_sg=10|30_** 符号查询
