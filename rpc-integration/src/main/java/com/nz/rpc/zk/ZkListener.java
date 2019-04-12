package com.nz.rpc.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;


@Slf4j
public class ZkListener  implements TreeCacheListener {


    public void eventReceived(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {

        try{
            switch (curatorEvent.getType()){
                case CREATE:
                    log.debug( curatorEvent.getPath() + " create" ); break;
                case DELETE:
                    log.debug( curatorEvent.getPath() + " DELETE" ); break;

                default:break;
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        try{
            ChildData data = treeCacheEvent.getData();

            switch (treeCacheEvent.getType()){
                case NODE_ADDED:
                    System.out.println("NODE_ADDED : "+ data.getPath() +"  数据:"+ new String(data.getData()));
                    break;
                case NODE_REMOVED:
                    System.out.println("NODE_REMOVED : "+ data.getPath() +"  数据:"+ new String(data.getData()));
                    break;
                case NODE_UPDATED:
                    System.out.println("NODE_UPDATED : "+ data.getPath() +"  数据:"+ new String(data.getData()));
                    break;

                default:break;
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public  void setListener(CuratorFramework client ){
        try{
            TreeCache treeCache = new TreeCache(client,"/app");
            treeCache.getListenable().addListener(new ZkListener());
            treeCache.start();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }


}
