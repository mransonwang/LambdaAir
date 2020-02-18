# 说明
本项目演示了在本地运行基于Spring Boot/Spring Cloud架构开发的微服务应用，包含服务注册、服务发现、服务网关、链路跟踪、断路器等基础功能。Spring Boot使用Red Hat Runtimes Spring Boot 2.1.6，Spring Cloud使用Greenwich.SR5，可平滑在本地或Red Hat OpenShift上进行部署，未来，也可使用OpenShift Service Mesh对本项目使用的Spring Cloud进行相应的替代。

# 应用说明

本应用是一个模拟查询航班报价的应用，由以下模块组成

````
Airports                          机场查询服务
Eureka                            微服务注册
Flights                           航班查询服务
Presentation                      应用前端
Sales                             票价查询服务
Zuul                              微服务网关
Zipkin                            微服务调用追踪
````

遵照微服务应用的通用模式设计，前端通过微服务网关Zuul对后端微服务Airports、Flights、Sales进行调用，将航班信息呈现给用户。所有微服务均注册到Eureka，后端微服务之间的调用，则是直接调用的方式，比如Flights对Airports的调用。服务调用的链路追踪信息发送到Zipkin，可通过查询界面进行观看。

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
mvn package
````

在以下目录生成可运行的.jar文件

````
Airports\target\airports-1.0-SNAPSHOT.jar
Eureka\target\eureka-1.0.0-RELEASE.jar
Flights\target\flights-1.0-SNAPSHOT.jar
Presentation\target\presentation-1.0-SNAPSHOT.jar
Sales\target\sales-1.0-SNAPSHOT.jar
Zuul\target\zuul-1.0.0-RELEASE.jar
````

从Spring Boot 2.0.0开始，Zipkin不再作为Spring Boot可编译项目存在，官方推荐从 [官网](https://zipkin.io) 下载打包好的.jar文件直接运行。在Zipkin目录中，已经准备好zipkin.jar文件。

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

通过 [http://localhost:6080](http://localhost:6080) 访问应用前端，在起始地址和目的地址尝试分别输入SEA和DFW，片刻后将会展现航班时间和报价，也可尝试输入其他起始地址或目的地址，比如LAS或SAN等。

访问 [http://localhost:8761](http://localhost:8761) 查看当前已经注册到Eureka的微服务列表。

访问 [http://localhost:9411](http://localhost:9411) 查看微服务链路跟踪和调用依赖关系。

# 将项目导入到Eclipse

项目本身是在Eclipse中进行开发调试，使用IDE也更加容易分析和学习项目。

启动Eclipse，工作目录选择LambdaAir或LambdaAir-master，Eclipse将会初始化工作目录，待初始化完毕后，将其下的六个子目录逐一导入：

````
File > Import... > Maven > Existing Maven Projects 
````
Eclipse将会自动构建并编译项目，然后处于就绪状态。

鼠标右键点击任意一个项目，比如Eureka，选择主执行类：

````
Run As > Java Application > EurekaApplication - com.example
````

使用如上方式可依次启动其他微服务。

# 项目的简要解读

## Eureka

需要注意的地方是pom.xml、application.yaml和主执行类

pom.xml的设置非常重要，它会引入项目需要的各种依赖类库，我们使用的是Spring Cloud Greenwich.SR5版本以及Red Hat Spring Boot 2.1.6版本，JDK则是1.8版本：

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Greenwich.SR5</spring-cloud.version>
		
		<spring-boot.version>2.1.6.RELEASE</spring-boot.version>
    	<spring-boot-maven-plugin.version>2.1.6.RELEASE</spring-boot-maven-plugin.version>
	</properties>

引入的依赖及依赖管理：

````
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
		</dependency>
	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>me.snowdrop</groupId>
				<artifactId>spring-boot-bom</artifactId>
				<version>2.1.6.SP3-redhat-00001</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
````
	
在pom.xml的末尾，添加了Red Hat的Maven仓库，这是因为项目中用到了Red Hat Spring Boot这个官方支持的类库，和纯开源的相比，红帽版本的Spring Boot不仅也完全开源，而且更能获得官方提供的技术支持，目前它属于Red Hat Runtimes中的一员。

    <repositories>
        <repository>
            <id>redhat-ga-repository</id>
            <name>Redhat GA Repository</name>
            <url>https://maven.repository.redhat.com/ga/all/</url>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>redhat-ga-repository</id>
            <name>Redhat GA Repository</name>
            <url>https://maven.repository.redhat.com/ga/all/</url>
        </pluginRepository>
    </pluginRepositories>

这里需要重点提及一下，由于Spring Boot/Spring Cloud微服务在开发编译时需要依赖中央的Maven构件仓库，强烈建议在本地搭建一个私有的构件仓库，以节省每次构建的时间，个人建议JFrog Artifactory。

application.yaml文件位于src/main/resources目录，它的主要内容：

````
spring:
  application:
    name: eureka

server: 
  port: 8761

eureka: 
  instance: 
    hostname: localhost    
  server:
    response-cache-update-interval-ms: 500
  client: 
    register-with-eureka: false
    fetch-registry: false
    service-url:
      default-zone: http://${eureka.instance.hostname}:${server.port}/eureka/
````

在主执行类com.example.EurekaApplication.java中，通过注解将将应用性质设置为Eureka Server：

````
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}
}
````

这样，一个Eureka微服务就开发完毕了。

## Airports

Airports会从一个.csv文件中加载机场信息以提供查询服务，它的主执行类AirportApplication.java内容如下：

````
@SpringBootApplication
@EnableDiscoveryClient
public class AirportsApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run( AirportsApplication.class, args );
	}
}
````

在model包中包含了Airport.java文件，主要为数据模型类，重要的实现类在service包中。

ApplicationInitializatioin.java主要实现查询数据的加载；Controller.java对外通过REST接口暴露服务，为了提供链路调用分析，在服务调用入口进行了俗称的埋点；而AirportService.java则是主要的处理逻辑所在。

## Flights/Sales

和Airports类似，不同的是，Flights会调用Airports的查询服务。

## Presentation

是基于JQuery的一个简单应用前端，当在查询框中输入起始机场及旅行时间，它会异步的向后台发送查询请求，请求首先到Zuul，然后通过Zuul分发给相应的服务提供者。

## Zuul

Zuul的构造和Eureka类似，作为微服务网关，是前端和后端的桥梁，在实际应用中，它的功能还包括流控、鉴权等。

## 微服务之间的调用方式

在本例中，所有对微服务的调用，在调用前都通过查询Eureka获得地址信息，比如Presentation对Zuul的调用，以及后端微服务间的互相调用。通过restTemplate的实例进行具体的调用发送动作，而负载均衡依赖的是Ribbon，由于本例中每个微服务仅仅启用单实例，因此Ribbon的作用无法直接体现。而Zuul对后台微服务的调用也采用了简单的转发方式，目的是为了体现在一个微服务应用中，服务之间的调用方式可以是很灵活的。