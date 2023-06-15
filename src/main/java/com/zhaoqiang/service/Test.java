package com.zhaoqiang.service;

import com.zhaoqiang.spring.ZhaoqiangApplicationContext;

public class Test {

    public static void main(String[] args) {
        ZhaoqiangApplicationContext zhaoqiangApplicationContext = new ZhaoqiangApplicationContext(AppConfig.class);
        Object userService = zhaoqiangApplicationContext.getBean("userService");
        Object userService1 = zhaoqiangApplicationContext.getBean("userService");
        System.out.println(userService);
        System.out.println(userService1);
    }
}
