## 流石API管理工具
可通过流程编排的方式完成API接口的低码配置功能；具体包含以下功能：
- 选择不同的数据库、表，配置查询条件，将其提供成接口向外提供；
- 通过编写SQL语句，将其转换成接口向外提供；SQL语句支持MyBatis语法；
- 通过前端配置功能完成外系统接口转发功能；
- 通过流程式的编排实现复杂的数据处理；
- 提供定时调用接口的功能；
- 支持JS/Groovy脚本执行来完成复杂逻辑处理；
- 自动生成接口文档，供调用方参考

## 适用场景
- 当已经有了大屏或者其它前端低码工具，而后端接口仍旧需要定制开发时，可以使用本工具来进行后端接口的快速配置与开发；
- 在数据类的交付项目中，需要进行一些简单的数据抽取、加工及开发对应的数据接口时，可以使用本工具来完成替代这部分功能，从而能够快速完成数据加工处理、接口开发，提升交付效率。

## 使用技术
- 前端：Vue3+Vite+ElementPlus+MonacoEditor等
- 后端：SpringBoot3+MyBatis-Plus+liquibase+Security等；

## 启动
- 前端：node18版本；其它版本未经测试；直接npm run dev 即可连接本地启动的后端服务进行开发；
- 后端：JDK17版本及以上；需要有mysql服务器，并创建对应的数据库，相关地址可在application-dev.yml文件中修改；修改完成后直接启动ServiceApplication.java类即可；数据库初始化脚本通过liquibase自动执行;
- 注意Token域名默认是ngq.com，默认情况下本地需加host  ui.ngq.com到localhost；前端启动后打开页面ui.ngq.com:3000即可访问页面；默认用户密码：admin/admin123

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
