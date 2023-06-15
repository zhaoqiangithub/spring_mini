package com.zhaoqiang.service;

import com.zhaoqiang.spring.Autowired;
import com.zhaoqiang.spring.Component;
import com.zhaoqiang.spring.Scope;

@Component
@Scope("singleton")
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);

    }
}
