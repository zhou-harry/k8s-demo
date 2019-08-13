FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD k8s-demo.jar /app.jar
#RUN bash -c 'touch app.jar'

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

COPY /so /so
#添加可执行权限
RUN chmod a+x -R  so
#创建so软链到/usr/lib下
RUN ln -s so /usr/lib

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Djava.library.path=/so","/app.jar"]
