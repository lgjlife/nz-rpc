package com.app.consumer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@ComponentScan(basePackages = "com")
@SpringBootApplication
public class AppConsumerApplication {
    
    public static void main(String args[]){
        SpringApplication.run(AppConsumerApplication.class,args);
    }
}
