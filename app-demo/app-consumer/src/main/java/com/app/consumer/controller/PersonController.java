package com.app.consumer.controller;


import com.app.common.service.PersonService;
import com.nz.rpc.anno.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {


    @RpcReference
    private PersonService personService;

    @GetMapping("/query")
    public  String  query(){
        log.debug("/person/query");
        CompletableFuture<String> future = personService.queryPerson("libai");

        try{
            return CompletableFuture.allOf(future).thenApply((T)->{

                String reslut = null;
                try{
                    reslut = future.get();
                    log.info("reslut = "+reslut);
                }
                catch(Exception ex){
                    log.error(ex.getMessage());
                }
                return reslut;
            }).get();

        }
        catch(Exception ex){
            log.error(ex.getMessage());

            return null;
        }


    }


}
