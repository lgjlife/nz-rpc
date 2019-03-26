package com.app.starter;

import org.springframework.util.StringUtils;

public class StarterService {
    private String config;

    public StarterService(String config) {
        this.config = config;
    }

    public String[] split(String separatorChar) {
        System.out.println("config = " + config);
        return StringUtils.split(this.config, separatorChar);
    }

}