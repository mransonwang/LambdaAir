# 说明
本项目演示了在本地运行基于Spring Boot/Spring Cloud架构开发的微服务应用，包含服务注册、服务发现、服务网关、链路跟踪、断路器等基础功能。Spring Boot使用Red Hat Runtimes Spring Boot 2.1.6，Spring Cloud使用Greenwich.SR5，可平滑在本地或Red Hat OpenShift上进行部署，未来，也可使用OpenShift Service Mesh对本项目使用的Spring Cloud进行相应的替代。

# 应用说明

本应用是一个模拟查询航班报价的应用，由以下模块组成

````
Airports                          提供机场查询服务
Eureka                            微服务注册
Flights                           航班查询服务
Presentation                      应用前端
Sales                             航班价格查询服务
Zuul                              微服务网关
Zipkin                            微服务调用追踪
````

应用遵照微服务应用的通用模式设计，前端通过微服务网关Zuul对后端微服务Airports、Flights、Sales进行调用，将航班信息呈现给使用者。所有微服务均注册到Eureka，后端微服务之间的调用，可通过Zuul调用，也可自行直接相互间调用。服务调用的链路追踪信息发送到Zipkin，可通过查询界面进行观察。

# 本地编译及运行

````
git clone https://github.com/mransonwang/LambdaAir.git
cd LambdaAir
````

由于项目文件较大，也可以通过 [LambdaAir on Spring Boot](https://github.com/mransonwang/LambdaAir/archive/master.zip) 直接下载打包文件(约70M)并解压到指定目录。

````
cd LambdaAir-master
````

使用Maven进行编译打包

````
mvn package -Popenshift
````
会在以下目录生成可运行的.jar文件

````
Airports\target\airports-1.0-SNAPSHOT.jar
Eureka\target\eureka-1.0.0-RELEASE.jar
Flights\target\flights-1.0-SNAPSHOT.jar
Presentation\target\presentation-1.0-SNAPSHOT.jar
Sales\target\sales-1.0-SNAPSHOT.jar
Zuul\target\zuul-1.0.0-RELEASE.jar

````

从Spring Boot 2.0.0开始，Zipkin不再作为Spring Boot可编译项目存在，官方推荐从 [官网](https://zipkin.io) 直接下载可运行的.jar文件直接运行，在Zipkin目录中，已经准备好zipkin.jar文件。

需要打开七个命令行窗口运行所有的微服务：

````
java -jar Eureka\target\eureka-1.0.0-RELEASE.jar
````
````
jar -jar Zipkin\zipkin.jar
````
````
jar -jar Airports\target\airports-1.0-SNAPSHOT.jar
````
````
jar -jar Flights\target\flights-1.0-SNAPSHOT.jar
````
````
jar -jar Presentation\target\presentation-1.0-SNAPSHOT.jar
````
````
jar -jar Sales\target\sales-1.0-SNAPSHOT.jar
````
````
jar -jar Zuul\target\zuul-1.0.0-RELEASE.jar
````

各微服务监听的端口分别如下：

````
Airports                          6010
Eureka                            8761
Flights                           6020
Presentation                      6080
Sales                             6030
Zuul                              6000
Zipkin                            9411
````
# 访问应用

通过 [http://localhost:6080](http://localhost:6080) 访问应用前端，在起始地址和目的地址尝试分别输入SEA和KFW，片刻后将会展现航班时间和报价，也可尝试输入其他起始地址或目的地址，比如LAS或SAN等。

访问 [http://localhost:8761](http://localhost:8761) 查看当前已经注册到Eureka的微服务列表。

访问 [http://localhost:9411](http://localhost:9411) 查看微服务链路跟踪和各自调用依赖关系。