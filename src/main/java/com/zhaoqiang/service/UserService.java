package com.zhaoqiang.service;

import com.zhaoqiang.spring.Autowired;
import com.zhaoqiang.spring.BeanNameAware;
import com.zhaoqiang.spring.Component;
import com.zhaoqiang.spring.Scope;

@Component
@Scope("singleton")
public class UserService implements BeanNameAware {

    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);

    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println(beanName);
    }
}
