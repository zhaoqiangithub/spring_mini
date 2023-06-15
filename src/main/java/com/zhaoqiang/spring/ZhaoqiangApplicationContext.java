package com.zhaoqiang.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class ZhaoqiangApplicationContext {

    /**
     * 配置类
     */
    private Class configClass;

    /**
     * beanDefination的map
     */
    private ConcurrentHashMap<String, BeanDefination> beanDefinationMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Object> singtonObjects  = new ConcurrentHashMap<>();

    /**
     * 构造方法
     *
     * @param clazz
     */
    public ZhaoqiangApplicationContext(Class clazz) {
        this.configClass = clazz;
        // 扫描
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScan.value();// com.zhaoqiang.service
            path = path.replace(".", "/");
            ClassLoader classLoader = ZhaoqiangApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);

            // 获取路径下的类文件
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    // 如果是class文件
                    if (fileName.endsWith(".class")) {
                        // 看是否加了Component注解，如果加了生成bean
                        String clasName = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));

                        clasName = clasName.replace("/", ".");
                        Class<?> aClass = null;
                        try {
                            aClass = classLoader.loadClass(clasName);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }


                        if (aClass.isAnnotationPresent(Component.class)) {
                            Component component = aClass.getAnnotation(Component.class);
                            String beanName = component.value();

                            // 注册成为beanDefination
                            // 因为如果是多例的，不应该在容器启动时就创建对象。所以先用BeanDefination包装一下
                            BeanDefination beanDefination = new BeanDefination();
                            if (aClass.isAnnotationPresent(Scope.class)) {
                                beanDefination.setClazz(aClass);
                                Scope annotation = aClass.getAnnotation(Scope.class);
                                String value = annotation.value();
                                beanDefination.setScope(value);
                            } else { // 默认单例
                                beanDefination.setScope("singleton");
                            }
                            // 将单例的BeanDefination放到单例池
                            beanDefinationMap.put(beanName, beanDefination);
                        }
                    }

                }
            }
        }
        // 单例池初始化
        for (String beanName : beanDefinationMap.keySet()) {
            BeanDefination beanDefination = beanDefinationMap.get(beanName);
            if (beanDefination.getScope().equals("singleton")) {
                Object bean = createBean(beanName, beanDefination);
                singtonObjects.put(beanName, bean);
            }

        }

    }

    /**
     * 创建bean方法
     * @return
     */
    private Object createBean(String beanName, BeanDefination beanDefination) {
        Class clazz = beanDefination.getClazz();
        try {
            // 调用无参构造
            Object instance = clazz.getConstructor().newInstance();
            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getBean(String beanName) {
        // 区分单例bean，还是多例bean
        BeanDefination beanDefination = beanDefinationMap.get(beanName);
        if (beanDefination == null) {
            throw new NullPointerException();
        } else {
            // 获取作用域
            String scope = beanDefination.getScope();
            if (scope.equals("singleton")) {
                Object bean = singtonObjects.get(beanName);
                if (bean == null) {
                    Object newBean = createBean(beanName, beanDefination);
                    singtonObjects.put(beanName, newBean);
                }
                return bean;
            } else {
                // 多例
                return createBean(beanName, beanDefination);
            }
        }
    }
}
