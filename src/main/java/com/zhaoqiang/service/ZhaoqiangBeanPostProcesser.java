package com.zhaoqiang.service;

import com.zhaoqiang.spring.BeanPostProcesser;
import com.zhaoqiang.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class ZhaoqiangBeanPostProcesser implements BeanPostProcesser {

    @Override
    public Object postProcessBeforeInilazation(String beanName, Object bean) {
        if (beanName.equals("userService")){
            System.out.println("ZhaoqiangBeanPostProcesser userService before excute");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInilazation(String beanName, Object bean) {
        if (beanName.equals("userService")){
            System.out.println("ZhaoqiangBeanPostProcesser userService after excute");

            // 用jdk动态代理产生一个代理对象
            return Proxy.newProxyInstance(ZhaoqiangBeanPostProcesser.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("切换逻辑");
                    // 不能传proxy这是代理对象，应该用bean原对象，调用method方法
                    return method.invoke(bean, args);
                }
            });
        }
        return bean;
    }
}
