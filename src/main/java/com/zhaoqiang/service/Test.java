package com.zhaoqiang.service;

import com.zhaoqiang.spring.ZhaoqiangApplicationContext;

public class Test {

    public static void main(String[] args) {
        ZhaoqiangApplicationContext zhaoqiangApplicationContext = new ZhaoqiangApplicationContext(AppConfig.class);
        UserInterface userService = (UserInterface)zhaoqiangApplicationContext.getBean("userService");
        System.out.println(userService);

        userService.test();
    }
}
