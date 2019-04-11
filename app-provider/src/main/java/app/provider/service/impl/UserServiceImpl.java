package app.provider.service.impl;


import com.app.common.service.UserService;
import com.nz.rpc.anno.RpcService;
import org.springframework.stereotype.Service;

import java.util.Date;


@RpcService
@Service
public class UserServiceImpl implements UserService {

    public  String queryName(Long id){
        return  "UserServiceImpl  queryName ：" + id +":" + new Date().toString();

    }

    public String queryName(String name, Long id){

        String str = "UserServiceImpl  queryName :" + name +":"+id+ new Date().toString();
        System.out.println(str);
        return  str;
    }

}
