package com.example.cloud.emqx.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/***
 @title EmqxConfig
 @description // TODO description
 @author ywang
 @version 1.0.0
 @create 2024/6/4 14:54
 **/
@ConfigurationProperties(prefix = "emqx")
@Getter
@Setter
@Component
public class EmqxConfig {
    private String broker;
    private String userName;
    private String passWord;
    private String topic;
    private int qos;
    private int keepAlive;
    private int connectionTimeout;
}
