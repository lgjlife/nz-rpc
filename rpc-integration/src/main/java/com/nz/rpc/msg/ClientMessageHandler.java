package com.nz.rpc.msg;

import com.nz.rpc.discover.ProviderConfigContainer;
import com.nz.rpc.discover.ProviderConfig;
import com.nz.rpc.loadbalance.LoadbalanceStrategy;
import com.nz.rpc.netty.client.NettyClient;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import com.nz.rpc.utils.uid.UidProducer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Slf4j
public class ClientMessageHandler {


    private static ClientMessageHandler instance= new ClientMessageHandler();
    private NettyClient nettyClient;
    private LoadbalanceStrategy  loadbalanceStrategy;
    private UidProducer uidProducer;
    private Map<Long,RpcResponse> resultMap = new ConcurrentHashMap<>();
    private static  final int requestTimeoutMs = 200;

    private Map<Long,ReentrantLock> lockMap = new ConcurrentHashMap<>();
    private Map<Long,Condition> conditionMap  =  new ConcurrentHashMap<>();


    public static ClientMessageHandler getInstance() {
        return instance;
    }


    /**
     *功能描述
     * @author lgj
     * @Description 执行远程调用
     * @date 4/17/19
     * @param:
     * @return:
     *
    */
    public long doRequest(RpcRequest request){



        //获取消费者
        Map<String, ProviderConfig> configMap = ProviderConfigContainer.getConfigMap();
        List<ProviderConfig> registryConfigLists = new ArrayList<>();
        configMap.forEach((k,v)->{
            if(v.getInterfaceName().equals(request.getInterfaceName())){
                registryConfigLists.add(v);
            }

        });
        //
        log.debug("服务提供者信息:{}",registryConfigLists);

        //负载均衡处理
        ProviderConfig registryConfig =  loadbalanceStrategy.select(registryConfigLists,null);
        log.debug("registryConfig = " + registryConfig);
        //设置唯一的请求id
        long uid = uidProducer.getUidForLong();
        request.setRequestId(uid);
        //初始化锁
        initLock(uid);

        //准备消息
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.APP_REQUEST_TYPE);
        nettyMessage.setHeader(header);
        nettyMessage.setBody(request);
        //发送消息
        nettyClient.getChannel(registryConfig.getHost(),registryConfig.getPort()).writeAndFlush(nettyMessage);

        return uid;
    }
    private void initLock(long uid){
        ReentrantLock lock = new ReentrantLock();
        lockMap.put(uid,lock);
        conditionMap.put(uid,lock.newCondition());
    }
    private void removeLock(long uid){
        lockMap.remove(uid);
        conditionMap.remove(uid);
    }


    /**
     *功能描述 
     * @author lgj
     * @Description  获取远程调用结果
     * @date 4/17/19
     * @param: 
     * @return: 
     *
    */
    public Object result(Long uid){

        if(resultMap.get(uid) == null){

            try{
                lockMap.get(uid).lock();
                //等待结果返回,超时则返回null
                conditionMap.get(uid).await(requestTimeoutMs, TimeUnit.MILLISECONDS);
                if(resultMap.get(uid) == null){
                    return null;
                }
                else {
                    Object result = resultMap.get(uid).getResult();
                    resultMap.remove(uid);
                    return result;
                }
            }
            catch(Exception ex){
                log.error(ex.getMessage());
                return null;
            }
            finally {
                lockMap.get(uid).unlock();
                removeLock(uid);
            }
        }
        else {
            return resultMap.get(uid).getResult();
        }
    }

    /**
     *功能描述
     * @author lgj
     * @Description 消息完成通知
     * @date 4/17/19
     * @param:
     * @return:
     *
    */
    public void finish(RpcResponse response){

        long uid = response.getResponseId();
        try{
            lockMap.get(uid).lock();
            resultMap.put(uid,response);
            conditionMap.get(uid).signal();
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            lockMap.get(uid).unlock();
        }

    }
}
