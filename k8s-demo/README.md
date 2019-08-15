# K8s部署Maven+Spring项目通过Sapjco3对接SAP系统
另有一篇文章可供参考：[Spring boot 通过RFC连接SAP部署到Docker](https://blog.csdn.net/wenfeifang/article/details/88676326)
### Sapjco3+Maven项目联调

*  创建本地Lib目录，并将官方sapjco3.jar放入该目录；
*  pom文件添加（本地）依赖依赖

```xml
    <dependency>
        <groupId>com.sap</groupId>
        <artifactId>sapjco3</artifactId>
        <version>3.0.19</version>
        <systemPath>${project.basedir}/lib/sapjco3.jar</systemPath>
        <scope>system</scope>
    </dependency>
```
***
问题：为什么不能直接将sapjco3.jar包打入中央仓库然后通过常规Maven依赖引入此文件？<br/>
答案：sapjco官方使用API中明确指定所引用的jar包名称必须为：sapjco3.jar(不能更改该名称)，
但是常规Maven依赖后的jar包名称是带版本号的，例如：sapjco3-3.0.19.jar
***
* 属性文件配置sap连接

***此处需要特别注意，因为是通过K8s容器部署，所以可能会存在在pod容器中无法访问外网的情况<br>
导致ashost配置的地址访问不到,具体解决方案见[K8s Pod 容器无法访问外网问题](https://note.youdao.com/web/#/file/3DCA404A774A457884E226AA65EFF0F9/note/WEB1f367668f41969265628dc980c0b6ebc/)***

```yaml
#sap连接配置
sap:
  jco:
    provider:
      destName: ABAP
      ashost: 10.1.118.71
      client: 120
      sysnr: 06
      lang: zh
      user: test
      passwd: test
      pool_capacity: 10
      peak_limit: 50
```
* 代码层面可以直接参照sap官方提供的案例


### K8s集成部署

* Windows系统中将sapjco3.dll放入system32目录下，代码编译时需要
* Linux系统需要将libsapjco3.so文件环境变量中，如下为sap官方操作指引：
```text
To install JCo for Linux copy the appropriate distribution package into an own arbitrary directory {sapjco3-path}. Next, change to the installation directory:
    cd {sapjco3-path}   [return]

and extract the archive:

    tar zxvf sapjco3-linux*3.0.19.tgz   [return]

Then add {sapjco3-path} to the LD_LIBRARY_PATH environment variable.
Finally, add {sapjco3-path}/sapjco3.jar to your CLASSPATH environment variable. 

```
***针对k8s部署场景，具体的libsapjco3.so文件操作见下面文档内容***
* 通过pom方式将libsapjco3.so文件打包进Dockerfile同级或下级目录（后续docker打镜像会用到）
```xml
<!--prepare-sapjco-->
<execution>
    <id>prepare-sapjco</id>
    <phase>validate</phase>
    <goals>
        <goal>copy-resources</goal>
    </goals>
    <configuration>
        <outputDirectory>${project.build.directory}/docker/so</outputDirectory>
        <resources>
            <resource>
                <directory>${project.basedir}/lib</directory>
                <includes>
                    <include>**/*.so</include>
                </includes>
            </resource>
        </resources>
    </configuration>
</execution>
```
* 准备springboot可执行jar文件<br>
（因为sapjco3.jar是通过本地依赖的，所以`spring-boot-maven-plugin`在打包的时候并不会将该文件打包进BOOT-INF/lib中）<br>
通过对`spring-boot-maven-plugin`简单修改可以简便快捷的将本地依赖打包,但是以下的打包方式是带版本号的（聪明的朋友一眼便能看出带版本号后对我们上面的操作的影响）<br>
`所以不能用此方法`
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <includeSystemScope>true</includeSystemScope>
    </configuration>
</plugin>
```
`正确的方式是：`
```xml
<!--default-resources-->
<execution>
    <id>default-resources</id>
    <phase>validate</phase>
    <goals>
        <goal>copy-resources</goal>
    </goals>
    <configuration>
        <outputDirectory>target/classes</outputDirectory>
        <resources>
            <resource>
                <directory>src/main/resources/</directory>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>${project.basedir}/lib</directory>
                <targetPath>BOOT-INF/lib/</targetPath>
                <includes>
                    <include>**/*.jar</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <targetPath>BOOT-INF/classes/</targetPath>
            </resource>
        </resources>
    </configuration>
</execution>
```
* 调整Dockerfile文件，将libsapjco3.so打入镜像中
```bash
FROM java:8-jre
VOLUME /tmp
ADD k8s-demo.jar /app.jar
#系统时间
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
#so为libsapjco3.so所在目录
COPY /so /so
#添加可执行权限
RUN chmod a+x -R  so
#创建so软链到/usr/lib下（此处有另一种更优的方法：在yaml文件中添加env,其路径指向当前/so即可【- name: LD_LIBRARY_PATH value: /so】）
#RUN ln -s /so/libuuid.so.1 /usr/lib && ln -s /so/ld-linux-x86-64.so.2 /usr/lib && ln -s /so/libsapjco3.so /usr/lib
# "-Djava.library.path=/so",
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```
* 调整yaml文件，指定引用的环境变量为Dockerfile中操作的so文件路径
```yaml
env:
    - name: LD_LIBRARY_PATH
      value: /so
```
