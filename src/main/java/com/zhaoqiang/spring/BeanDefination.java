package com.zhaoqiang.spring;

public class BeanDefination {

    // bean的class类
    private Class clazz;

    // bean 单例还是多例
    private String Scope;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return Scope;
    }

    public void setScope(String scope) {
        Scope = scope;
    }
}
