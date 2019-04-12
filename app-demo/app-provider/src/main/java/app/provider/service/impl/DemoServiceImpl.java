package app.provider.service.impl;


import com.app.common.service.DemoService;
import com.nz.rpc.anno.RpcService;
import org.springframework.stereotype.Service;

import java.util.Date;


@RpcService
@Service
public class DemoServiceImpl  implements DemoService {


    public  String setName(Long id){
        return  "UserServiceImpl  queryName :" +id + ":"+ new Date().toString();
    }

    public  String setName(String name, Long id){
        return  "UserServiceImpl  queryName :" + name +":"+id+ new Date().toString();
    }
}
