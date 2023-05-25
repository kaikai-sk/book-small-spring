package com.springframework.test.event;

import com.springframework.context.ApplicationListener;

import java.util.Date;

public class CustomEventListener implements ApplicationListener<CustomEvent> {
    @Override
    public void onApplicationEvent(CustomEvent event) {
        System.out.println(String.format("收到%s的消息，时间为：%s", event.getSource(), new Date()));
        System.out.println(String.format("消息：id = %s, msg = %s", event.getId(), event.getMessage()));
    }
}
