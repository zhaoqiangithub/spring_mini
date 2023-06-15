package com.zhaoqiang.service;

import com.zhaoqiang.spring.BeanPostProcesser;
import com.zhaoqiang.spring.Component;

@Component
public class ZhaoqiangBeanPostProcesser implements BeanPostProcesser {

    @Override
    public void postProcessBeforeInilazation(String beanName, Object object) {
        if (beanName.equals("userService")){
            System.out.println("ZhaoqiangBeanPostProcesser userService before excute");
        }
    }

    @Override
    public void postProcessAfterInilazation(String beanName, Object object) {
        if (beanName.equals("userService")){
            System.out.println("ZhaoqiangBeanPostProcesser userService after excute");
        }
    }
}
