package com.zhaoqiang.spring;

/**
 * bean实现此接口，spring回调此接口，用于实现bean的一些初始化操作。
 * spring只负责调用，不负责业务逻辑实现
 * 实现在bean的位置
 */
public interface InitializingBean {

    /**
     * 初始化方法
     */
    public void afterPropertiesSet();
}
