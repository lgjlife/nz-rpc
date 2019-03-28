# 基于netty和zookeeper的RPC框架

## 使用技术
* Spring  Boot
* Netty: 底层通信实现
* Zookeeper：服务注册中心
## 说明
## 目标
| 目标|备注|
| ----| ---|
| springboot搭建项目|完成|
| rpc-client和rpc-server配置成starter|完成|
| Zookeeper作为配置中心|未完成|
| Zookeeper注册服务提供者信息|完成|
| 消费者从Zookeeper中获取服务提供者信息|完成|
| netty通信|完成|
| 客户端接口调用反射拦截|完成|
| 提供端接收客户端请求并反射执行相关方法返回结果|完成|
| 多种序列化方式实现|完成。主要用于zookeeper，netty通信。|
| 负载均衡实现|未完成|
| 调用链路检测实现|未完成|
| 服务分组路由|未完成|
| 服务依赖关系分析|未完成|
| 服务降级|未完成|
## 使用说明
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

