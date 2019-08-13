FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD k8s-demo.jar /app.jar
#RUN bash -c 'touch app.jar'

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

COPY libsapjco3.so /libsapjco3.so
RUN chmod a+x -R  libsapjco3.so

COPY libuuid.so.1 /libuuid.so.1
RUN chmod a+x -R  libuuid.so.1
RUN ln -s /libuuid.so.1 /usr/lib

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Djava.library.path=/","/app.jar"]
