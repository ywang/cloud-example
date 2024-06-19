package com.example.cloud.emqx.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.example.cloud.emqx.config.EmqxConfig;
import com.example.cloud.emqx.message.EmqxMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/***
 @title EmqxConsumer
 @description // TODO description
 @author ywang
 @version 1.0.0
 @create 2024/6/4 14:53
 **/
@Component
@Slf4j
public class EmqxConsumer {

    @Resource
    private EmqxConfig emqxConfig;

    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    public void init() {
        try {
            MqttClient client = new MqttClient(emqxConfig.getBroker(), applicationName, new MemoryPersistence());
            // 连接参数
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(emqxConfig.getUserName());
            options.setPassword(emqxConfig.getPassWord().toCharArray());
            options.setConnectionTimeout(emqxConfig.getConnectionTimeout());
            options.setKeepAliveInterval(emqxConfig.getKeepAlive());

            // 设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    log.error("connectionLost: {}", cause.getMessage(), cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    log.info("messageArrived topic: {}, qos: {}, payload: {}", topic, message.getQos(), payload);

                    EmqxMessage receiveMsg = null;
                    try {
                        receiveMsg = JSONObject.parseObject(payload, EmqxMessage.class);
                    } catch (Exception e) {}

                    if (receiveMsg == null) {
                        return;
                    }
                    String to = Optional.ofNullable(receiveMsg.getTo()).orElse("demon");
                    String body = String.format("{'to':'%s', 'body':'Hello, I'm subscribe_client'}", to);
                    try {
                        // 发布消息
                        client.publish(to, new MqttMessage(body.getBytes()));
                    } catch (Exception e) {
                        log.error("publish error: to:{}, body:{}", to, body, e);
                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.info("deliveryComplete---------{}", token.isComplete());
                }

            });
            client.connect(options);
            client.subscribe(emqxConfig.getTopic(), emqxConfig.getQos());

        } catch (Exception e) {
            log.error("mqtt error: {}", e);
        }
    }

}
