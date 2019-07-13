# 基于netty和zookeeper的RPC框架

## 说明
nzRpc是一个基于netty和zookeeper的RPC框架，使用netty作为底层socket通信框架。使用Zookeeper作为注册中心。
* 服务提供者启动时会向服务注册中心注册相关信息[注册信息](https://github.com/lgjlife/nz-rpc/blob/master/rpc-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Fdiscover%2FProviderConfig.java)
* 消费者启动时，会获取注册信息，并缓存在应用中
* 消费者会监听注册信息的变化，比如服务提供者上线，并进行更新。
* 服务调用不经过注册中心，运行过程中注册中心宕机不影响服务调用。

目前仅支持SpringBoot应用，提供端和消费端只要引入注解[@RpcService](https://github.com/lgjlife/nz-rpc/blob/master/rpc-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Fanno%2FRpcService.java)和[@RpcReference](https://github.com/lgjlife/nz-rpc/blob/master/rpc-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Fanno%2FRpcReference.java)，并在application.yml中进行端口和IP配置，即可轻易使用，详细可参见如下<a href="#use">使用说明</a>

*整体架构图*

![](https://github.com/lgjlife/nz-rpc/blob/master/doc/1.png)
    
![](https://github.com/lgjlife/nz-rpc/blob/master/doc/2.png)

## 基本特性

* 模块配置成SpringBoot Starter，引入POM依赖即可
* 每一个节点既可以作为服务消费者，也可以作为服务提供者
* 接入简单，通过添加注解[RpcReference](https://github.com/lgjlife/nz-rpc/blob/master/rpc-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Fanno%2FRpcReference.java)和[RpcService](https://github.com/lgjlife/nz-rpc/blob/master/rpc-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnz%2Frpc%2Fanno%2FRpcService.java),即可零配置启动。
* 使用zookeeper实现服务注册与发现
* 服务提供者，服务消费者，服务注册中心之间为长连接
* 服务提供者信息缓存在消费者本地缓存，服务注册中心挂掉不影响服务调用，但新启动应用将无法加入
* 支持服务下线通知，服务上线通知，上线和下线将会更新应用本地服务提供者的缓存信息
* 使用Netty作为底层NIO通信框架
* 心跳机制检测连接状态
* 自定义消息协议
* 连接失败自动重连
* 对端关闭则释放本端连接资源
* 动态代理支持JDK和CGLIB方式，默认为JDK方式，可配置
* 序列化支持JDK,Fastjson,Protostuff,Hessian方式,默认为Hessian方式，可配置
* 服务容错策略: failfast快速失败,failsafe安全失败,failback失败定时重试
* 通过责任链设计模式实现请求过程的链式处理，包括集群容错处理，超时处理，负载均衡选择，请求发送，各个功能间无耦合，扩展方便
* 请求支持同步请求和异步请求，异步请求的返回值需为CompletableFuture类型
* 负载均衡实现：随机,加权随机,轮轮询，加权轮询，最小时间法,一致性Hash
* 全局唯一ID实现，雪花算法、redis生成、zookeeper生成
* 分布式锁实现:redis/zookeeper实现

## 性能测试
使用Jmeter进行性能测试
jvm参数：-server -Xms1024m -Xmx1024m -Xmn800m -XX:MetaspaceSize=800m -XX:MaxMetaspaceSize=1000m
1.日志等级为(debug)的情况下，单次请求在4ms左右，并发在500时，平均响应时间为50ms左右，并发在5000时，平均响应时间已经升到600ms左右。
2.日志等级为(error)的情况下,即使并发到10000，平均响应时间也是在5ms左右。
3.日志框架更换为log4j2，使用异步方式打印日志，5000并发情况下单个请求平均耗时在10ms内
## 模块介绍


 ├─── nzrpc
 
     ├──── rpc-integration  rpc实现框架

     ├──── app-demo   使用demo        

        ├────── app-common    用于存放demo公共接口

        ├────── app-consumer  消费者
        
        ├────── app-provider  既是消费者又是服务提供者
         
        ├────── app-provider1 服务提供者
        
        ├────── app-dubbo-consumer  dubbo 消费者
               
        ├────── app-dubbo-provider  dubbo 提供者
                

## 类说明

    ├─── nz/

      ├─── rpc/
         ├─── anno/        
             ├─── RpcReference                      注解在服务消费端接口引用上，用于标示该引用是消费者接口
             ├─── RpcService                        注解在服务提供端接口实现类上，用于标示该类为消费者提供服务
                   
         ├─── cluster/  
            ├─── AbstractClusterFaultConfig         集群错误配置类，用于用户配置接口对应的从错策略
            ├─── ClusterFault                       容错策略接口
            ├─── ClusterFaultAutoconfiguration      集群容错自动配置类，配置各个容错策略Bean
            ├─── ClusterFaultHandler                集群容错处理类
            ├─── FailbackClusterFault               失败定时重试处理方式
            ├─── FailfastClusterFault               快速失败，立即抛出异常给调用者
            ├─── FailoverClusterFault               失败自动切换，未实现
            ├─── FailsafeClusterFault               失败安全，不作处理，返回null          
         
         ├─── constans/
            ├─── RpcClientConstans                  用于存放一些静态变量，比去key
            
         ├─── context/
            ├─── ClientContext                      客户端请求过程上下文，存放一些通用属性
                
         ├─── discover/
            ├─── AbstractServiceDiscover            服务注册与发现抽象类    
            ├─── DiscoverAutoConfiguration  　　     服务注册与发现自动配置类           
            ├─── ProviderConfig                     服务注册配置类
            ├─── ProviderConfigContainer        
            ├─── ZookeeperServiceDiscover           实现类，用于服务发现     
            ├─── ZookeeperServiceRegister　　　　    实现类，用于服务注册
         
         ├─── exception/
            ├─── MessageSendFailException
            ├─── RpcRuntimeException
         
         ├─── executor/
            ├───  TraceExecutorService              重写线程池,支持打印线程池异常错误堆栈
            
         ├─── interceptor/
            ├─── exception/
                ├─── AddInterceptorException        添加拦截器异常
                ├─── RequestTimeOutException        请求超时异常
            ├─── impl/
                ├─── ClusterFaultToleranceInterceptor  集群容错拦截器
                ├─── RpcClientRequestInterceptor        执行最终的客户端请求拦截器
                ├─── ServiceSelectInterceptor          负载均衡处理拦截器，用于选择Server
                ├─── TimeOutInterceptor                请求超时处理拦截器
            ├─── ClientInterceptorAutoConfiguration    责任链自动配置类
            ├─── Interceptor                           拦截器接口
            ├─── InterceptorChain                      拦截器操作类，用于添加，移除拦截器等操作
         
         ├─── invocation/
            ├─── client/
                ├─── ClientInvocation                  用于责任链中传递消息的接口
                ├─── RpcClientInvocation               实现类
         
         ├─── loadbalabce/
            ├─── exception/
                ├─── LoadbalanceException              负载均衡异常
            ├─── AbstractLoadbalanceConfig             用于用户配置接口对应的负载均衡该策略
            ├─── LeastActiveLoadbalanceStrategy        最小调用时延
            ├─── LoadbalanceAutoConfig                 负载均衡自动配置类
            ├─── LoadbalanceStrategy　　　　　　　　　   负载均衡策略接口
            ├─── LoadbanlanceHandler                   负载均衡操作类
            ├─── PollingLoadbalanceStrategy　　　　　   轮询算法
            ├─── RandomLoadbalanceStrategy       　　  随机负载均衡    
            ├─── UniformityHashLoadbalanceStrategy　   一致性hash算法
            ├─── WeightPollingLoadbalanceStrategy　　  加权轮询算法
            ├─── WeightRandomLoadbalanceStrategy　　   加权随机负载均衡
            
         ├─── lock/
            ├─── exception/
                ├───　RequestLockException　　　       请求锁异常
            ├─── redis/
                ├─── RedisAutoConfiguration　　        Redis自动配置类
                ├─── RedisClient　　　　　　　　         Redis操作客户端
                ├─── RedisLockUtil　　　　　　　        Redis锁实现
                ├─── RedisPoolClient　　　　　　        Redis连接池
            ├─── zk/
                ├─── ZkClient　　　　　　　　　          zookeeper客户端操作
                ├─── ZkLockUtil　　　　　　　　         zookeeper分布式锁实现
            ├─── Lock　　　　　　　　　　　　　　         分布式锁接口
            ├─── LockProperties　　　　　　　　         Lock模块属性配置类
         ├─── msg/
            ├─── request/ 
                ├─── AbstractRequestHandler
                ├─── RequestHandler
                ├─── RpcRequestHandler
            ├─── ClientMessageHandler　　　　　　       客户端消息综合处理类
            ├─── MsgAutoConfiguration　　　　　　       自动配置类
            ├─── RpcRequest　　　　　　　　　　　　       netty通信请求pojo
            ├─── RpcResponse 　　　　　　　　　　　       netty通信响应pojo
            ├─── ServerMessageHandler　　　　　　        server消息综合处理类
         
         ├─── netty/
            ├─── client/
                ├─── handle/
                    ├───　ClientChannelInboundHandler　 数据输入处理handler
                    ├───　ClientChannelOutboundHandle　　数据输出处理handler
                    ├───　HeartbeatRequestHandler　　　　心跳请求处理类
                    ├───　NettyChannelHandler　　　　　　 责任连管理类
                ├───　NettyClient　　　　　　　　　　　　　 netty客户端相关处理
                ├───　NettyClientAutoConfiguration　　　　客户端自动配置类
            ├─── coder/
                ├───  MessageBodyUtil                    消息的body编解码
                ├───  MessageIndex                       消息中的各个字段的相对位置
                ├───　NettyMessageDecode                 消息解码处理
                ├───　NettyMessageEncoder　　　　　　　　  消息编码处理
            ├─── message/
                ├───　Header                              消息头
                ├───　MessageType　　　　　　　　　　　　　　消息类型
                ├───　NettyMessage　　　　　　　　　　　　　 整个消息
            ├─── server/
                ├─── handle/
                    ├───　ChildChannelHandler　　　　　　　服务端责任连管理
                    ├───　HeartbeatResponseHandler　　　　 服务端心跳处理类
                    ├───　ServerChannelInboundHandler　　　服务端数据输入处理handler
                    ├───　ServerChannelOutboundHandle     服务端数据输出处理handler
                ├───　NettyServer                          netty服务端相关处理
                ├───　NettyServerAutoConfiguration　　　　　netty服务端自动配置类
         
         ├─── properties/
            ├─── RpcProperties　　　　　　　　　　           系统所有的配置，相关配置在application.yml中进行配置
         
         ├─── proxy/
            ├─── ProxyCreate　　　　　　　　　　             动态代理创建类接口
            ├─── CglibProxyCreate　　　　　　　　            cglib动态代理创建类实现类
            ├─── JavassitProxyCreate　　　　　　            Javassit动态代理创建类实现类
            ├─── JdkProxyCreate                            jdk动态代理创建类实现类
            ├─── ProxySelector                             用于根据配置选择对应的动态代理创建类
            ├─── RpcInvoker　　　　　　　　　　　             接口动态代理类,用于执行方法拦截
            ├─── RpcProxyFactory　　　　　　　　             动态代理类生成工厂
         
         ├─── serialization/
            ├─── AbstractSerialize                         序列化抽象接口
            ├─── FastjsonSerializeUtil 　　　　　　          Fastjson实现类
            ├─── HessianSerializeUtil　　　　　　            Hessian实现类
            ├─── JdkSerializeUtil       　　　　　　         Jdk实现类
            ├─── ProtostuffSerializeUtil　　　　　　         Protostuff实现类
            ├─── SerializationAutoConfiguration　　         序列化自动配置类
            ├─── SerializationCreate　　　　　　　 　         序列化创建工厂
       
         ├─── uid/
            ├─── UidProducer　　　　　　　　　                分布式唯一ID生成接口
            ├─── UUidProducer                               使用JDK的UUID来生成唯一ID
            ├─── CustomProducer                             自定义唯一id生成方式
            ├─── UidAutoConfiguration　　　　                唯一id模块自动配置类
            ├─── ZkUidProducer 　　　　　　　                 zookeeper方式生成唯一id
         
         ├─── zk/
            ├─── ZkCli                                     zookeeper操作类，用于连接读写操作
            ├─── ZkCreateConfig　　　　　　　　　　           zk创建path时的配置类
            ├─── ZkListener　　　　　　　　　　　　            zk监听器
            ├─── ZookeeperAutoConfigure　　　　　            zk自动配置类
            ├─── ZookeeperPath　　　　　　　　　　             zk路径的工具类
         
          
 
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
