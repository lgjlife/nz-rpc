# 基于netty和zookeeper的RPC框架

## 使用技术
* Spring  Boot: 2.1
* Netty: 4.1.底层通信实现
* Zookeeper：3.4.服务注册中心
## 说明
nzRpc是一个基于netty和zookeeper的RPC框架，使用netty作为底层socket通信框架。使用Zookeeper作为注册中心。
* 服务提供者启动时会向服务注册中心注册相关信息[注册信息](https://github.com/lgjlife/nz-rpc/blob/master/rpc-support%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Frpcsupport%2Futils%2FRegistryConfig.java)
* 消费者启动时，会获取注册信息，并缓存在应用中
* 消费者会监听注册信息的变化，比如服务提供者上线，并进行更新。
* 服务调用不经过注册中心，运行过程中注册中心宕机不影响服务调用。

目前仅支持SpringBoot应用，提供端和消费端只要引入注解[@RpcService]()和[@RpcReference]()，并在application.yml中进行端口和IP配置，即可轻易使用，详细可参见如下<a href="#use">使用说明</a>
## 目标
| 目标|备注|
| ----| ---|
| springboot+maven搭建项目|完成|
| rpc-client和rpc-server配置成starter|完成|
| Zookeeper注册服务提供者信息|完成|
| 消费者从Zookeeper中获取服务提供者信息|完成|
| Zookeeper作为配置中心|完成,服务提供者注册服务信息，消费者获取服务信息并缓存，并监听服务信息的变化，如果发生变化，则重新拉取信息|
| 多种序列化方式实现|完成。主要用于zookeeper，netty通信。[序列化repo](https://github.com/lgjlife/serialization)|
| netty通信|完成|
| 客户端接口调用反射拦截|完成|
| 提供端接收客户端请求并反射执行相关方法返回结果|完成|
| 负载均衡实现|未完成|
| 调用链路检测实现|未完成|
| 服务分组路由|未完成|
| 服务依赖关系分析|未完成|
| 服务降级|未完成|
## 使用说明 

<h1 id="use"></h1>

### 服务提供端
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

### 服务消费端


* 依赖引入
```xml

```
* application.xml配置
```java

```

