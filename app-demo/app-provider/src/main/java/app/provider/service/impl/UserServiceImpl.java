package app.provider.service.impl;


import com.app.common.service.UserService;
import com.nz.rpc.anno.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;


@Slf4j
@RpcService
@Service
public class UserServiceImpl implements UserService {

    public  String queryName(Long id){
        log.debug("UserServiceImpl queryName " + id);
        return  "UserServiceImpl  queryName ：" + id +":" + new Date().toString();

    }

    public String queryName(String name, Long id){
        log.debug("UserServiceImpl queryName {} - {}",name,id);
        String str = "UserServiceImpl  queryName :" + name +":"+id+ new Date().toString();
        System.out.println(str);
        return  str;
    }

}
