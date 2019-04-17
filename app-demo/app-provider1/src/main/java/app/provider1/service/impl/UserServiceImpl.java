package app.provider1.service.impl;


import com.app.common.service.UserService;
import com.nz.rpc.anno.RpcService;
import org.springframework.stereotype.Service;

import java.util.Date;


@RpcService
@Service
public class UserServiceImpl implements UserService {

    public  String queryName(Long id){
        return  "UserServiceImpl 111 queryName ：" + id +":" + new Date().toString();

    }

    public String queryName(String name, Long id){

        String str = "UserServiceImpl 111  queryName :" + name +":"+id+ new Date().toString();
        System.out.println(str);
        return  str;
    }

}
