package com.zhaoqiang.spring;

/**
 * bean实现此接口后，
 * beanName回调，spring可以将创建的beanName回调，传给bean
 */
public interface BeanNameAware {

    public void setBeanName(String beanName);
}
