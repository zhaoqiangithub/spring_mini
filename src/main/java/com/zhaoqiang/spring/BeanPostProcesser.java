package com.zhaoqiang.spring;

public interface BeanPostProcesser {

    /**
     *
     * @param beanName
     * @param object
     */
    public void postProcessBeforeInilazation(String beanName, Object object);

    /**
     *
     * @param beanName
     * @param object
     */
    public void postProcessAfterInilazation(String beanName, Object object);

}
