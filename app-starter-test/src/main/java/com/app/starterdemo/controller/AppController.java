package com.app.starterdemo.controller;


import com.app.starter.StarterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @Autowired
    private StarterService starterService;


    @RequestMapping("/app")
    public String[] starterTest() {
        String[] splitArray = starterService.split(",");


        for(String v:splitArray){
            System.out.println(v);
        }

        return  splitArray;
    }

}
