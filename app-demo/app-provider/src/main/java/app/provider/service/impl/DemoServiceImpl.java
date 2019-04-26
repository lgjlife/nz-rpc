package app.provider.service.impl;


import com.app.common.service.DemoService;
import com.nz.rpc.anno.RpcService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;


@RpcService
@Service
public class DemoServiceImpl  implements DemoService {


    public  String setName(Long id){
        return  "DemoServiceImpl  setName :" +id + ":"+ new Date().toString()+ "   " + new Random().nextInt(1000);
    }

    public  String setName(String name, Long id){
        return  "DemoServiceImpl  queryName :" + name +":"
                +id
                + new Date().toString()
                + "   " + new Random().nextInt(1000);
    }
}
