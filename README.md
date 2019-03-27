# 基于netty和zookeeper的RPC框架

## 使用技术
* Spring  Boot
* Netty: 底层通信实现
* Zookeeper：服务注册中心

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

