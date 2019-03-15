package com.nz.rpc.client.beanlife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com")
@SpringBootApplication
public class BeanlifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeanlifeApplication.class, args);
    }

}

