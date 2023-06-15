package com.zhaoqiang.service;

import com.zhaoqiang.spring.*;

@Component
@Scope("singleton")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);

    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println(beanName);
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("userService初始化");


    }
}
