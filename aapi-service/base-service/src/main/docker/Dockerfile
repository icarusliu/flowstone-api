FROM openjdk:17-jre

ENV SPRING_OUTPUT_ANSI_ENABLED=NEVER \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS=""

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/#[[\$]]#TZ /etc/localtime && echo '#[[\$]]#TZ' > /etc/timezone

CMD echo "The application will start in #[[\$]]#{JHIPSTER_SLEEP}s..." && \
    sleep #[[\$]]#{JHIPSTER_SLEEP} && \
    java #[[\$]]#{JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar

EXPOSE 80

ADD *.jar /app.jar
