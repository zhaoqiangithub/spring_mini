package com.zhaoqiang.service;

import com.zhaoqiang.spring.Component;
import com.zhaoqiang.spring.Scope;

@Component
@Scope("prototype")
public class OrderService {

    public String test() {
        System.out.println(111);
        return null;
    }
}
