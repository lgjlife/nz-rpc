package com.nz.rpc.zk;

import com.nz.rpc.properties.RpcProperties;
import com.utils.serialization.AbstractSerialize;
import com.utils.serialization.HessianSerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ZkCli {

    private CuratorFramework client;
    private RpcProperties properties;
    private AbstractSerialize serialize = HessianSerializeUtil.getSingleton();


    public void setProperties(RpcProperties properties) {
        this.properties = properties;
    }

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

    public  String createPath(ZkCreateConfig config){
       try{

           if (false == checkExists(config.getPath())) {
               log.debug("path [{}] not exist ，create path ....", config.getPath());
               String result  =  client.create()
                       .creatingParentsIfNeeded()
                       .withMode((config == null?CreateMode.PERSISTENT:config.getCreateMode()))
                       .forPath(config.getPath());
               if(result == null){
                   log.debug("create [{}] fail,maybe the path is exist! ",config.getPath());
               }
               else {
                   log.debug("create [{}] success! ",config.getPath());
               }

               return result;

           } else {
               log.debug("create [{}] fail,maybe the path is exist! ",config.getPath());
           }


       }
       catch(Exception ex){
           log.error(ex.getMessage());
       }

       return  null;

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

    public  Stat setData(String path,Object object){

        try{
            byte[] data = serialize.serialize(object);

           Stat result = client.setData().forPath(path,data);
           return  result;
        }
        catch(Exception ex){
            log.error("path [{}] set the type [{}] data exception! {}",path,ex.getMessage());
        }

        return  null;
    }

    public  int getVersion(String path){
        try{
            return  client.setData().forPath(path,new byte[0]).getVersion();
        }
        catch(Exception ex){
            log.error("path [{}] set the type [{}] data exception! {}",path,ex.getMessage());
        }
        return  0;
    }


    public List<String> getChildren(String path){
        try{
            List<String> paths =  client.getChildren().forPath(path);
            return  paths;
        }
        catch(Exception ex){
            log.error("get [{}] child fail!",path,ex);
            return  new ArrayList<>();
        }

    }

    /**
     *功能描述
     * @author lgj
     * @Description  添加监听器
     * @date 4/30/19
     * @param: 　eventHandler: 监听器处理函数
     * 　　　　　path：　监听路径
     * @return:
     *
    */
    public  void setListener(ListenerEventHandler eventHandler,String path){
        try{
            TreeCache treeCache = new TreeCache(this.client,path);
            treeCache.getListenable().addListener(new ZkListener(eventHandler));
            treeCache.start();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }


}
