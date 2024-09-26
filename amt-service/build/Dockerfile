# 以下是基础镜像，不包含nginx/web/mariadb
# FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-slim
# 以下是集成镜像，包含有nginx/web/mariadb的镜像
from swr.cn-east-3.myhuaweicloud.com/icarus-tools/flowstone-amt:v1.0.2

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/#[[\$]]#TZ /etc/localtime && echo '#[[\$]]#TZ' > /etc/timezone

CMD nginx && service mariadb start && java -Djava.security.egd=file:/dev/./urandom -jar /app.jar

EXPOSE 80

ADD *.jar /app.jar
ADD dist/ /root/html