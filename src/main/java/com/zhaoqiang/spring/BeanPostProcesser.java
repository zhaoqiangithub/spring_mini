package com.zhaoqiang.spring;

public interface BeanPostProcesser {

    /**
     *
     * @param beanName
     * @param object
     */
    public Object postProcessBeforeInilazation(String beanName, Object object);

    /**
     *
     * @param beanName
     * @param object
     */
    public Object postProcessAfterInilazation(String beanName, Object object);

}
