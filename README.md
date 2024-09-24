## 流石API管理工具
可通过流程编排的方式完成API接口的低码配置功能；具体包含以下功能：
- 选择不同的数据库、表，配置查询条件，将其提供成接口向外提供；
- 通过编写SQL语句，将其转换成接口向外提供；SQL语句支持MyBatis语法；
- 通过前端配置功能完成外系统接口转发功能；
- 通过流程式的编排实现复杂的数据处理；
- 提供定时调用接口的功能；
- 支持JS/Groovy脚本执行来完成复杂逻辑处理；
- 自动生成接口文档，供调用方参考
- 便于扩展的节点类型，目前支持SQL、Table、Http、JS、Groovy五种节点，可方便在此基础上扩展更多节点，比如Kafka推送节点等；
- 关于数据源：目前仅支持MySql，后续扩展支持更多数据库类型；

## 适用场景
- 当已经有了大屏或者其它前端低码工具，而后端接口仍旧需要定制开发时，可以使用本工具来进行后端接口的快速配置与开发；
- 在数据类的交付项目中，需要进行一些简单的数据抽取、加工及开发对应的数据接口时，可以使用本工具来完成替代这部分功能，从而能够快速完成数据加工处理、接口开发，提升交付效率。

## 使用技术
- 前端：Vue3+Vite+ElementPlus+MonacoEditor等
- 后端：SpringBoot3+MyBatis-Plus+liquibase+Security等；

## 启动
### 开发启动
- 前端：node18版本；其它版本未经测试；直接npm run dev 即可连接本地启动的后端服务进行开发；
- 后端：JDK17版本及以上；需要有mysql服务器，并创建对应的数据库，相关地址可在application-dev.yml文件中修改；修改完成后直接启动ServiceApplication.java类即可；数据库初始化脚本通过liquibase自动执行;
- 注意Token域名默认是ngq.com，默认情况下本地需加host  ui.ngq.com到localhost；前端启动后打开页面ui.ngq.com:3000即可访问页面；默认用户密码：admin/admin123

### 使用docker直接启动现有镜像
镜像地址：swr.cn-east-3.myhuaweicloud.com/icarus-tools/flowstone-amt:v1.0.0

镜像内已集成mariadb/nginx，直接启动后即可通过浏览器打开页面进行测试；

启动命令示例：

```cmd
docker run -d -e SPRING_SECURITY_DOMAIN=localhost -p 3001:80 swr.cn-east-3.myhuaweicloud.com/icarus-tools/flowstone-amt:v1.0.1
```

注意：
- mariadb数据在容器销毁后，所修改的数据会丢失，如果需要保留，需要增加磁盘映射到/var/lib/mysql；
- 通过浏览器打开localhost:3001即可使用相关功能；默认登录用户与密码：admin/admin123
- SPRING_SECURITY_DOMAIN可以修改域名，如不指定，默认是ngq.com，以下命令将域名修改成test.com
    ```cmd
    docker run -d -e SPRING_SECURITY_DOMAIN=test.com -p 3001:80 swr.cn-east-3.myhuaweicloud.com/icarus-tools/flowstone-amt:v1.0.1
    ```
   修改成test.com或者其它域名后（即所有非localhost的域名），如果未做域名解析，需要本地配置hosts才可正常访问；

## 界面
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

## 后续计划
- 扩展更多的数据库类型支持；
- 扩展支持MQ节点
- 应用式接口鉴权模式
- 其它

# 其它说明
本工具基础代码使用[流石代码生成](https://gitee.com/changkang/flowstone-code-generator)工具（Idea的插件）生成，这个插件也是本人所写的一个插件，通过定义代码模板的方式一键生成基于MyBatisPlus的基础代码及Liquibase脚本、前端页面；感兴趣的可以关注一拨。

# 版本变更记录
## v1.0.0
- 基础版本

## v1.0.1
- 完善接入方认证鉴权功能
- 接口类型为其它节点或者请求参数时，增加JSONPath解析功能，可通过a.b的方式进行多层级变量解析；
- 处理数据源无法录入密码的bug；
- 调试日志增加JSON格式化功能；
- 处理日志模块缺失导致前端无法启动问题；