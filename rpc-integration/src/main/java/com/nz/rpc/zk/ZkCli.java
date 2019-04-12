package com.nz.rpc.zk;

import com.nz.rpc.properties.RpcProperties;
import com.utils.serialization.AbstractSerialize;
import com.utils.serialization.HessianSerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;


@Slf4j
public class ZkCli {

    private CuratorFramework client;
    private RpcProperties properties;
    private AbstractSerialize serialize = HessianSerializeUtil.getSingleton();
    /**
     *功能描述
     * @author lgj
     * @Description  连接到zookeeper
     * @date 3/27/19
     * @param:
     * @return:
     *
     */
    public void connect() {
        //拒绝策略
        RetryPolicy retryPolicy
                = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(properties.getZookeeperAdress(),
                retryPolicy);
        client.start();
        log.debug("zookeeper[{}] client start....,state = {}",properties.getZookeeperAdress(),client.getState());
    }

    public boolean checkExists(String path){
        try{
            Stat result  = client.checkExists().forPath(path);
            if (result == null){
                return  false;
            }
            return  true;

        }
        catch(Exception ex){
            log.error("path[{}] check exist exception [{}] ",path,ex.getMessage());
        }
        return  false;
    }

    public  void createPath(String path){
       try{

           if (false == checkExists(path)) {
               log.debug("目录{}不存在，创建目录....", path);
               client.create().creatingParentsIfNeeded().forPath(path);

           } else {
               log.debug("目录{}已经存在,获取目录信息", path);
           }


       }
       catch(Exception ex){
           log.error(ex.getMessage());
       }

    }

    public  Object getData(String path,Class type){

        try{
            byte[] readData = client.getData().forPath(path);
            Object object = serialize.deserialize(readData,type);
            return  object;

        }
        catch(Exception ex){
            log.error("path [{}] read the type [{}] data exception! {}",path,type,ex.getMessage());
            return  false;
        }
    }

    public  void setData(String path,Object object){

        try{
            byte[] data = serialize.serialize(object);

            client.setData().forPath(path,data);
        }
        catch(Exception ex){
            log.error("path [{}] set the type [{}] data exception! {}",path,ex.getMessage());
        }
    }

}
