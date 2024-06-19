package com.example.cloud.emqx.message;

import lombok.Data;

import java.io.Serializable;

/***
 @title EmqxMessage
 @description // TODO description
 @author ywang
 @version 1.0.0
 @create 2024/6/4 15:09
 **/
@Data
public class EmqxMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String to;
    private String body;
}
