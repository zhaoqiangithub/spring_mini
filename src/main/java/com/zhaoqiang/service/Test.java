package com.zhaoqiang.service;

import com.zhaoqiang.spring.ZhaoqiangApplicationContext;

public class Test {

    public static void main(String[] args) {
        ZhaoqiangApplicationContext zhaoqiangApplicationContext = new ZhaoqiangApplicationContext(AppConfig.class);
        UserService userService = (UserService)zhaoqiangApplicationContext.getBean("userService");
        System.out.println(userService);

        userService.test();
    }
}
