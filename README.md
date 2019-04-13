# 基于netty和zookeeper的RPC框架

## 说明
nzRpc是一个基于netty和zookeeper的RPC框架，使用netty作为底层socket通信框架。使用Zookeeper作为注册中心。
* 服务提供者启动时会向服务注册中心注册相关信息[注册信息](https://github.com/lgjlife/nz-rpc/blob/master/rpc-support%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Frpcsupport%2Futils%2FRegistryConfig.java)
* 消费者启动时，会获取注册信息，并缓存在应用中
* 消费者会监听注册信息的变化，比如服务提供者上线，并进行更新。
* 服务调用不经过注册中心，运行过程中注册中心宕机不影响服务调用。

目前仅支持SpringBoot应用，提供端和消费端只要引入注解[@RpcService](https://github.com/lgjlife/nz-rpc/blob/master/rpc-support%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Frpcsupport%2Fannotation%2FRpcService.java)和[@RpcReference](https://github.com/lgjlife/nz-rpc/blob/master/rpc-support%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Frpcsupport%2Fannotation%2FRpcReference.java)，并在application.yml中进行端口和IP配置，即可轻易使用，详细可参见如下<a href="#use">使用说明</a>

*模块说明*

## 基本特性

* 模块配置成SpringBoot Starter，引入POM依赖即可
* 接入简单，通过添加注解[RpcReference](https://github.com/lgjlife/nz-rpc/blob/master/rpc-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Fanno%2FRpcReference.java)和[RpcService](https://github.com/lgjlife/nz-rpc/blob/master/rpc-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Fanno%2FRpcService.java),即可零配置启动。
* 使用zookeeper实现服务注册与发现
* 使用Netty作为底层NIO通信框架
* 动态代理支持JDK和CGLIB方式，默认为JDK方式，可配置
* 序列化支持JDK,Fastjson,Protostuff,Hessian方式,默认为Hessian方式，可配置
* 服务下线通知
* 负载均衡实现：随机,加权随机,轮轮询，加权轮询，最小时间法
* 

## 模块介绍


 ├─── nzrpc
 
     ├──── rpc-integration  rpc实现框架

     ├──── app-demo   使用demo

        ├────── app-common    用于存放demo公共接口

        ├────── app-consumer  消费者
        
        ├────── app-provider  既是消费者又是服务提供者
         
        ├────── app-provider1 服务提供者

## 使用技术
* Spring  Boot-2.1
* Netty-4.1 底层通信实现
* Zookeeper-3.4 服务注册中心

<h1 id="use"></h1>

## 使用说明 



###  服务提供端
* 依赖引入
```xml
<!-- nzRpc 服务端starter -->
<dependency>
    <groupId>com.nz.rpc</groupId>
    <artifactId>rpc-server-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

```
* application.xml配置
```yaml
nzrpc:
  server:
    #zookeeper host
    zhost: 127.0.0.1
    #zookeeper port
    zport: 2181

```
* 定义公共接口
```java
public interface IUserService {

    String queryName(long id);
}
```
* 实现上述接口

需要在接口上添加注解@RpcService
```java
@Service
@RpcService(interfaceClass=IUserService.class)
public class UserService  implements IUserService {

    @Override
    public String queryName(long id) {
        return "UserService:"+id;
    }
}
```

###  服务消费端


* 依赖引入
```xml

```
* application.xml配置
```java

```

