# 基于netty和zookeeper的RPC框架

## 说明
nzRpc是一个基于netty和zookeeper的RPC框架，使用netty作为底层socket通信框架。使用Zookeeper作为注册中心。
* 服务提供者启动时会向服务注册中心注册相关信息[注册信息](https://github.com/lgjlife/nz-rpc/blob/master/rpc-support%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Frpcsupport%2Futils%2FRegistryConfig.java)
* 消费者启动时，会获取注册信息，并缓存在应用中
* 消费者会监听注册信息的变化，比如服务提供者上线，并进行更新。
* 服务调用不经过注册中心，运行过程中注册中心宕机不影响服务调用。

目前仅支持SpringBoot应用，提供端和消费端只要引入注解[@RpcService](https://github.com/lgjlife/nz-rpc/blob/master/rpc-support%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Frpcsupport%2Fannotation%2FRpcService.java)和[@RpcReference](https://github.com/lgjlife/nz-rpc/blob/master/rpc-support%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Frpcsupport%2Fannotation%2FRpcReference.java)，并在application.yml中进行端口和IP配置，即可轻易使用，详细可参见如下<a href="#use">使用说明</a>

*模块说明*

![](https://github.com/lgjlife/nz-rpc/blob/master/doc/1.png)
    
![](https://github.com/lgjlife/nz-rpc/blob/master/doc/2.png)

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

## 类说明

├─── nz

     ├─── rpc
         ├─── anno         
             ├─── RpcReference  注解在服务消费端接口引用上，用于标示该引用是消费者接口
             ├─── RpcService   注解在服务提供端接口实现类上，用于标示该类为消费者提供服务
                   
         ├─── common  
                
         ├─── discover
            ├─── AbstractServiceDiscover        服务注册与发现抽象类    
            ├─── DiscoverAutoConfiguration  　　服务注册与发现自动配置类           
            ├─── RegistryConfig                 服务注册配置类
            ├─── ZookeeperServiceDiscover       实现类，用于服务发现     
            ├─── ZookeeperServiceRegister　　　　实现类，用于服务注册
         
         ├─── loadbalabce
            ├─── LoadbalanceStrategy　　　　　　　负载均衡策略接口
            ├─── RandomLoadbalanceStrategy       随机负载均衡    
            ├─── WeightRandomLoadbalanceStrategy　加权随机负载均衡
         
         ├─── msg
            ├─── RpcRequest　　　　　　　　　　　　netty通信请求pojo
            ├─── RpcResponse 　　　　　　　　　　　netty通信响应pojo
         
         ├─── netty
            ├─── client
                ├─── handle
                    ├───　ClientChannelInboundHandler
                    ├───　ClientChannelOutboundHandle
                    ├───　NettyChannelHandler
                ├───　NettyClient
                ├───　NettyClientAutoConfiguration
            ├─── coder
                ├───　MsgCoder
                ├───　MsgDecoder
            ├─── message
                ├───　Header
                ├───　MessageType
                ├───　NettyMessage
            ├─── server
                ├─── handle
                    ├───　ChildChannelHandler
                    ├───　ServerChannelInboundHandler
                    ├───　ServerChannelOutboundHandle
                ├───　NettyServer
                ├───　NettyServerAutoConfiguration
         
         ├─── properties
            ├─── RpcProperties　　　　　　　　　　系统所有的配置，相关配置在application.yml中进行配置
         
         ├─── proxy
            ├─── ProxyCreate　　　　　　　　　　  动态代理创建类接口
            ├─── CglibProxyCreate　　　　　　　　cglib动态代理创建类实现类
            ├─── JavassitProxyCreate　　　　　　 Javassit动态代理创建类实现类
            ├─── JdkProxyCreate                jdk动态代理创建类实现类
            ├─── ProxySelector                 用于根据配置选择对应的动态代理创建类
            ├─── RpcInvoker　　　　　　　　　　　 接口动态代理类,用于执行方法拦截
            ├─── RpcProxyFactory　　　　　　　　 动态代理类生成工厂
         
         ├─── serialization
            ├─── AbstractSerialize                序列化抽象接口
            ├─── FastjsonSerializeUtil 　　　　　　 Fastjson实现类
            ├─── HessianSerializeUtil　　　　　　　 Hessian实现类
            ├─── JdkSerializeUtil       　　　　　　Jdk实现类
            ├─── ProtostuffSerializeUtil　　　　　　Protostuff实现类
            ├─── SerializationAutoConfiguration　　序列化自动配置类
            ├─── SerializationCreate　　　　　　　 　序列化创建工厂
         ├─── utils
            ├─── uid
                ├─── UidProducer　　　　　　　　　分布式唯一ID生成接口
                ├─── UUidProducer               使用JDK的UUID来生成唯一ID
                ├─── CustomProducer             自定义唯一id生成方式
                ├─── UidAutoConfiguration　　　　唯一id模块自动配置类
                ├─── ZkUidProducer 　　　　　　　 zookeeper方式生成唯一id
         
         ├─── zk
            ├─── ZkCli                          zookeeper操作类，用于连接读写操作
            ├─── ZkCreateConfig　　　　　　　　　　zk创建path时的配置类
            ├─── ZkListener　　　　　　　　　　　　zk监听器
            ├─── ZookeeperAutoConfigure　　　　　zk自动配置类
            ├─── ZookeeperPath　　　　　　　　　　zk路径的工具类
         
          
 
## 使用技术
* Spring  Boot-2.1
* Netty-4.1 底层通信实现
* Zookeeper-3.4 服务注册中心

<h1 id="use"></h1>

## 使用说明 

* 每一个节点既可以作为服务消费者，也可以作为服务提供者，无需多余配置，使用上述配置即可。
* 启动应用需先安装zookeeper,并启动,应用的默认地址端口配置是按照zookeeper的默认参数设置，可不进行配置，只要添加相关注解即可。
* 相关使用实例参照[app-demo](https://github.com/lgjlife/nz-rpc/tree/master/app-demo)



###  服务提供端
* 依赖引入
```xml
<dependency>
    <groupId>com.nz.rpc</groupId>
    <artifactId>rpc-integration-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>

```
* application.yml配置
可以不进行配置　，按默认配置即可启动,默认配置见[RpcProperties](https://github.com/lgjlife/nz-rpc/blob/master/rpc-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Fproperties%2FRpcProperties.java)

```yaml
nzrpc: 
#zookeeper 连接地址，有多个通过逗号","间隔
  zookeeper:
    address: "127.0.0.1:2181"
#netty  监听地址端口
  netty:
    port: 8121
# @RpcReference　注解的引用所在的包名，有多个使用逗号","间隔
  scanPackage: "com,org"
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
@RpcService
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
<dependency>
    <groupId>com.nz.rpc</groupId>
    <artifactId>rpc-integration-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>

```
* application.yml配置
可以不进行配置　，按默认配置即可启动,默认配置见[RpcProperties](https://github.com/lgjlife/nz-rpc/blob/master/rpc-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Fproperties%2FRpcProperties.java)

```yaml
nzrpc: 
#zookeeper 连接地址，有多个通过逗号","间隔
  zookeeper:
    address: "127.0.0.1:2181"
#netty  监听地址端口
  netty:
    port: 8121
# @RpcReference　注解的引用所在的包名，有多个使用逗号","间隔
  scanPackage: "com,org"
```
```java
@Slf4j
@RestController
public class DemoController {
    //使用RpcReference注解
    @RpcReference
    private UserService userService;

    @GetMapping("/demo")
    public  void  demo(){
       //接口调用
        userService.queryName("qqwq",13546L);
    }
}
```
