# 1. 流石API管理工具
可通过流程编排的方式完成API接口的低码配置功能；具体包含以下功能：
- 选择不同的数据库、表，配置查询条件，将其提供成接口向外提供；
- 通过编写SQL语句，将其转换成接口向外提供；SQL语句支持MyBatis语法；
- 通过前端配置功能完成外系统接口转发功能；
- 通过流程式的编排实现复杂的数据处理；
- 提供定时调用接口的功能；
- 支持JS/Groovy脚本执行来完成复杂逻辑处理；
- 自动生成接口文档，供调用方参考
- 便于扩展的节点类型，目前支持SQL、Table、Http、JS、Groovy五种节点，可方便在此基础上扩展更多节点，比如Kafka推送节点等；
- 外部数据源支持：目前支持MySql与SqlServer，可按需快速扩展新的支持；

# 2. 适用场景
- 当已经有了大屏或者其它前端低码工具，而后端接口仍旧需要定制开发时，可以使用本工具来进行后端接口的快速配置与开发；
- 在数据类的交付项目中，需要进行一些简单的数据抽取、加工及开发对应的数据接口时，可以使用本工具来完成替代这部分功能，从而能够快速完成数据加工处理、接口开发，提升交付效率。

# 3. 系统架构
## 3.1 系统功能架构
<img src="https://github.com/user-attachments/assets/7f2366b2-30ea-49df-98ed-4c71b64d5ab3" width="500"/>

其中标黄部分暂未实现；

## 3.2 系统技术架构
<img src="https://github.com/user-attachments/assets/ad7f3da5-9085-43cb-93aa-d13f05720629" width="500"/>

## 3.3 系统交互架构
<img src="https://github.com/user-attachments/assets/9c14cc1b-3e61-4fae-800a-e850e20439ce" width="400"/>

## 3.4 系统部署架构
<img src="https://github.com/user-attachments/assets/ddd95b16-3419-45f6-b034-a860962250b0" width="400"/>

# 4. 启动
## 4.1 开发启动
- 前端：node18版本；其它版本未经测试；直接npm run dev 即可连接本地启动的后端服务进行开发；
- 后端：JDK17版本及以上；需要有mysql服务器，并创建对应的数据库，相关地址可在application-dev.yml文件中修改；修改完成后直接启动ServiceApplication.java类即可；数据库初始化脚本通过liquibase自动执行;
- 注意Token域名默认是localhost，端启动后打开页面localhost:3000即可访问页面；默认用户密码：admin/admin123

## 4.2 使用docker直接启动现有镜像
镜像地址：swr.cn-east-3.myhuaweicloud.com/icarus-tools/flowstone-amt:v1.0.3

镜像内已集成mariadb/nginx，直接启动后即可通过浏览器打开页面进行测试；

启动命令示例：

```cmd
docker run -d -e SPRING_SECURITY_DOMAIN=localhost -p 3001:80 swr.cn-east-3.myhuaweicloud.com/icarus-tools/flowstone-amt:v1.0.3
```

注意：
- docker镜像相对于代码可能会有版本过期的问题，如若在使用镜像过程中存在某些问题，可以尝试自行使用代码进行启动，或者在线进行反馈；
- mariadb数据在容器销毁后，所修改的数据会丢失，如果需要保留，需要增加磁盘映射到/var/lib/mysql；
- 通过浏览器打开localhost:3001即可使用相关功能；默认登录用户与密码：admin/admin123
- SPRING_SECURITY_DOMAIN可以修改域名，如不指定，默认是localhost，以下命令将域名修改成test.com
    ```cmd
    docker run -d -e SPRING_SECURITY_DOMAIN=test.com -p 3001:80 swr.cn-east-3.myhuaweicloud.com/icarus-tools/flowstone-amt:v1.0.3
    ```
   修改成test.com或者其它域名后（即所有非localhost的域名），如果未做域名解析，需要本地配置hosts才可正常访问；

## 4.3 docker打包全量（前端与后端、DB打包到一个镜像中）
进入项目build目录，执行build.bat + 版本号，比如
```cmd
.\build.bat v1.0.3
```
生成的docker镜像为：amt-service:v1.0.3，通过docker images可以看到对应镜像，然后使用4.2中的命令启动镜像即可；

注意需要npm /mvn 可通过命令行执行，并且启动docker程序，否则无法构建；

## 4.4 构建、部署与运行
### 4.4.1 前端
前端构建直接使用npm run build命令，然后将dist目录传到服务器，并配置nginx即可，nginx示例如下：
```nginx
 server {
  listen 80;

  location / {
          root /root/html;
          index index.html;
          try_files $uri $uri/ /index.html;
  }

  location /api/ {
          proxy_pass http://localhost:8080/;
  }

  location /ws/front {
          proxy_pass http://localhost:8080;
          proxy_http_version 1.1;
          proxy_read_timeout 360s;
          proxy_redirect off;
          proxy_set_header Upgrade $http_upgrade;
          proxy_set_header Connection "upgrade"; #配置连接为升级连接
          proxy_set_header Host $host:$server_port;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header REMOTE-HOST $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  }
}
```
注意端口是按要按实际情况进行修改，同时如果线上部署需要配置相应域名；

### 4.4.2 后端
#### 4.4.2.1 准备
项目依赖MySql或者MariaDB，在启动后端前需要先准备好，并创建对应的数据库；

建表等通过liquibase直接进行，不需要额外处理；

注意修改配置文件application-dev.yaml中的数据库连接信息，生产环境建议复制该文件创建新的环境配置文件然后进行修改，比如application-prd.yaml；

#### 4.4.2.1 编译与打包
第一步进入项目根目录；

第二步使用以下命令编译（或者在idea中直接点击mvn相应按钮进行编译）：
```cmd
mvn clean package
```
此时会在/base-service/target下生成对应的jar包；

#### 4.4.2.2 运行
一般有两种方式运行后端服务

##### 4.4.2.2.1 直接运行
进入生成的jar的目录，或者将jar复制到指定目录，输入以下命令可直接运行
```cmd
java -jar base-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```
dev可以修改成自己的环境名称，比如如果想要使用application-prd.yaml配置文件，则替换为prd

##### 4.4.2.2.2 docker运行
- dockerfile目录：/base-service/src/main/docker
- 将生成的jar复制到该目录；
- 启动docker，进入docker目录，执行构建命令：
  ```cmd
  docker build -t amt-service:1.0.3 .
  ```
- 使用docker启动项目：
  ```cmd
  docker run -d -e SPRING_SECURITY_DOMAIN=localhost -p 8080:8080 amt-service:1.0.3
  ```
  具体参数根据实际场景修改

# 5. 界面
接口文档
![image](https://github.com/user-attachments/assets/eb8aa31e-370b-4278-a9ea-bfd8d788ec1f)

接口列表
![image](https://github.com/user-attachments/assets/d79e210a-56fd-49c5-a458-8a818e9cceb9)

接口处理流程配置
![image](https://github.com/user-attachments/assets/df1e0a39-0cf9-4d07-9997-b73fe9559ac2)

JS脚本编辑
![image](https://github.com/user-attachments/assets/8c12c48d-a4c1-40e2-9ca0-44f6a4432600)

HTTP节点配置
![image](https://github.com/user-attachments/assets/e336193a-6a8e-4008-b076-e4716f6069cc)

SQL节点
![image](https://github.com/user-attachments/assets/71654473-4f9d-492a-bffa-fd746dee7823)

支持MyBatis语法的SQL编辑及执行
![image](https://github.com/user-attachments/assets/0568c561-0e93-4f06-a011-dda3c34af094)

接口测试
![image](https://github.com/user-attachments/assets/b1506bf8-713a-4826-b6e0-8fe8cfce4946)

# 6. 接口调用

## 6.1 基础说明
管理前端测试时调用的接口为发布前的接口，此处只适合做测试用，而给到用户前端或者外系统调用时，需要将接口进行发布，使用发布后的接口进行调用。

通过前端调用接口时的路径：http://localhost:3000/api/dua/*，其中 * 表示的是所配置的接口路径；

通过后端服务直接调用时的路径：http://localhost:8080/dua/*；

比如通过前端调用的curl语句：
```curl
curl 'http://localhost:3000/api/dua/test/js' \
  -H 'Cookie: access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdXBlciI6dHJ1ZSwibmlja25hbWUiOiJhZG1pbiIsInRlbmFudElkIjoic3lzIiwiaWQiOiJRSVFJTklVQkkiLCJ1c2VybmFtZSI6ImFkbWluIn0.OUSwVEJVqzSYQIfNou6jtv0N4Mil7H8bFXBOruquS70'
```

注意如果指定过域名需要将localhost修改成对应域名；端口也需进行相应修改；

## 6.2 通过管理端token调用
接口调用时需要包含有相应的Token信息，这个Token可以在管理端登录时获取；也可以手动调用该登录接口。

## 6.3 通过客户端模式调用
- 进入客户端管理功能，新增客户端；
- 使用clientId/clientSecret进行token换算，示例：
  ```curl
  curl http://localhost:3000/api/auth/client-login?clientId=1838749028435689474&clientSecret=f812e3ea708446b98fd81fd4940cdec5
  ```
  调用后将会返回一串字符串，复制在后续接口中调用
- 进行实际接口调用，使用cookie或者header中添加authorization，比如：
  ```curl
  curl 'http://localhost:3000/api/dua/test/js' \
  -H 'Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdXBlciI6dHJ1ZSwibmlja25hbWUiOiJhZG1pbiIsInRlbmFudElkIjoic3lzIiwiaWQiOiJRSVFJTklVQkkiLCJ1c2VybmFtZSI6ImFkbWluIn0.OUSwVEJVqzSYQIfNou6jtv0N4Mil7H8bFXBOruquS70'
  ```

# 7. 其它说明
本工具基础代码使用[流石代码生成](https://gitee.com/changkang/flowstone-code-generator)工具（Idea的插件）生成，这个插件也是本人所写的一个插件，通过定义代码模板的方式一键生成基于MyBatisPlus的基础代码及Liquibase脚本、前端页面；感兴趣的可以关注一拨。

# 8. 版本变更记录
## 主分支
- 数据源增加SQLServer支持；

## v1.0.3 
- 接口可配置成游客模式进行免登录调用；
- 增加发布接口调用白名单控制功能；
- 增加限流功能；默认使用guava实现限流，如果有配置redis时使用redisson进行限流
- 增加客户端管理及客户端鉴权功能；
- 完善文档功能，增加调用Curl示例；
- 增加手动保存测试数据功能，同时完善输入配置功能，保存测试数据时，如果未配置输入参数，则会使用测试数据解析输入参数进行填充
- 修复游客模式带来的测试接口时跳转登录页面问题；
- 增加客户端接口授权功能，可为客户端授权所有接口或者部分指定接口；
- 增加缓存功能；
- 运行日志增加按客户端查询功能；

## v1.0.2
- 增加各种节点类型的示例接口
- 处理SQL节点测试参数配置异常问题；
- 处理编辑器第一次进入时无法编辑的问题； 
- 优化WebSocket处理逻辑；
- 处理表节点获取表清单时，无法获取手动创建的表的问题； 

## v1.0.1
- 完善接入方认证鉴权功能
- 接口类型为其它节点或者请求参数时，增加JSONPath解析功能，可通过a.b的方式进行多层级变量解析；
- 处理数据源无法录入密码的bug；
- 调试日志增加JSON格式化功能；
- 处理日志模块缺失导致前端无法启动问题；

## v1.0.0
- 基础版本

