package com.app.starter;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("example.controller")
public class StarterServiceProperties {

    private String config;

    public void setConfig(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }

}
