package app.provider.service.impl;

import com.app.common.service.PersonService;
import com.nz.rpc.anno.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CompletableFuture;


@Slf4j
@RpcService
@Service
public class PersonServiceImpl implements PersonService {


    @Override
    public CompletableFuture<String> queryPerson(String name) {

        log.debug("PersonServiceImpl queryPerson :" + name);
        CompletableFuture<String> future = new CompletableFuture<String>();
        String result = "PersonServiceImpl  queryPerson :" +name + ":"+ new Date().toString()+ "   " + new Random().nextInt(1000);
        future.complete(result);
        return future;

    }

    @Override
    public String doSomeThing() {
        return null;
    }
}
