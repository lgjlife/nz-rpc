package com.demo.test.controller;


import com.demo.test.service.TestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TreeSet;

@RestController
public class TestController {

    @Autowired
    private TestRequest request;

    @GetMapping("/nzrpc/test")
    public TreeSet nzrpc(){

        return request.startTest("nzrpc");

    }

    @GetMapping("/dubbo/test")
    public TreeSet dubbo(){

        return request.startTest("dubbo");
    }

}
