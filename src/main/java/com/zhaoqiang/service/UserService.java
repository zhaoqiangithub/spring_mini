package com.zhaoqiang.service;

import com.zhaoqiang.spring.Component;
import com.zhaoqiang.spring.Scope;

@Component("userService")
@Scope("prototype")
public class UserService {

    public String test() {
        System.out.println(111);
        return null;
    }
}
