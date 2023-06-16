package com.zhaoqiang.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * 单例池
     */
    private ConcurrentHashMap<String, Object> singtonObjects  = new ConcurrentHashMap<>();

    /**
     * BeanPostProcesser集合
     */
    private List<BeanPostProcesser> beanPostProcesserList = new ArrayList<>();

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
                        Class<?> aClass;
                        try {
                            aClass = classLoader.loadClass(clasName);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        if (aClass.isAnnotationPresent(Component.class)) {
                            Component component = aClass.getAnnotation(Component.class);

                            // BeanPostProcesser处理逻辑
                            if (BeanPostProcesser.class.isAssignableFrom(aClass)) {
                                try {
                                    BeanPostProcesser beanPostProcesser = (BeanPostProcesser) aClass.newInstance();
                                    beanPostProcesserList.add(beanPostProcesser);

                                } catch (InstantiationException e) {
                                    throw new RuntimeException(e);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            String beanName = component.value();
                            // 如果component没有设置value，没有beanName
                            if (beanName.equals("")) {
                                // 获取类名，首字母小写
                                String simpleName = aClass.getSimpleName();
                                beanName = Introspector.decapitalize(simpleName);
                            }

                            // 注册成为beanDefination
                            // 因为如果是多例的，不应该在容器启动时就创建对象。所以先用BeanDefination包装一下
                            BeanDefination beanDefination = new BeanDefination();
                            beanDefination.setClazz(aClass);

                            if (aClass.isAnnotationPresent(Scope.class)) {
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

            // 依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                // 如果属性有Autowired，则给属性创建实例
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    // 此处beanName不能用传进来的，那个是原来bean的name，应该是属性的bean的名称
                    field.set(instance, getBean(field.getName()));
                }
            }

            // Aware回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware)instance).setBeanName(beanName);
            }

            // 调用BeanPostProcesser的初始化前
            for (BeanPostProcesser beanPostProcesser : beanPostProcesserList) {
                instance = beanPostProcesser.postProcessBeforeInilazation(beanName, instance);
            }

            // 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean)instance).afterPropertiesSet();
            }

            // 调用BeanPostProcesser的初始化后
            for (BeanPostProcesser beanPostProcesser : beanPostProcesserList) {
                instance = beanPostProcesser.postProcessAfterInilazation(beanName, instance);
            }

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

    /**
     * 获取单例或者多例bean，如果是单例，优先从单例池拿
     * 如果是多例bean，直接调用createBean创建
     *
     * @param beanName
     * @return
     */
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
