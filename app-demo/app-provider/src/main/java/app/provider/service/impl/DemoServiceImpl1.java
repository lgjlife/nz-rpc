package app.provider.service.impl;


import com.app.common.service.DemoService;
import com.nz.rpc.anno.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Slf4j
@RpcService
@Service
public class DemoServiceImpl1 implements DemoService {


    public  String setName(Long id){
        return  "DemoServiceImpl  setName :" +id + ":"+ new Date().toString()+ "   " + new Random().nextInt(1000);
    }

    public  String setName(String name, Long id){
        try{

            Thread.sleep(3000);
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        return  "DemoServiceImpl  queryName :" + name +":"
                +id
                + new Date().toString()
                + "   " + new Random().nextInt(1000);
    }
}
