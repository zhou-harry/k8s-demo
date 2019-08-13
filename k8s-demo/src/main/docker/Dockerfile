FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD k8s-demo.jar /app.jar
#RUN bash -c 'touch /app.jar'

RUN chmod a+x -R  /libsapjco3.so

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Djava.library.path=./","/app.jar"]
