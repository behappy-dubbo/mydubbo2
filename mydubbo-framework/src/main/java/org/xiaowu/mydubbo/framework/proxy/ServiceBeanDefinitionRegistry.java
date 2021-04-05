package org.xiaowu.mydubbo.framework.proxy;

import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.xiaowu.mydubbo.framework.annotations.MyDubboRefrence;
import org.xiaowu.mydubbo.framework.discovery.RedisDiscoveryCenter;
import org.xiaowu.mydubbo.framework.pool.ExecutorServicePool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * 用于Spring动态注入自定义接口
 */
public class ServiceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Properties properties = new Properties();
        InputStream is = this.getClass().getResourceAsStream("/application.properties");
        try {
            if (is == null){
                is = this.getClass().getResourceAsStream("/application.yml");
            }
            properties.load(is);
        } catch (IOException ignored) {}
        Set<Class<?>> typesAnnotatedWith = new Reflections(properties.getProperty("dubbo.interface.path"), Arrays.asList(
                new SubTypesScanner(false)//允许getAllTypes获取所有Object的子类, 不设置为false则 getAllTypes 会报错.默认为true.
                ,new MethodParameterNamesScanner()//设置方法参数名称 扫描器,否则调用getConstructorParamNames 会报错
                ,new MethodAnnotationsScanner() //设置方法注解 扫描器, 否则getConstructorsAnnotatedWith,getMethodsAnnotatedWith 会报错
                ,new MemberUsageScanner() //设置 member 扫描器,否则 getMethodUsage 会报错, 不推荐使用,有可能会报错 Caused by: java.lang.ClassCastException: javassist.bytecode.InterfaceMethodrefInfo cannot be cast to javassist.bytecode.MethodrefInfo
                ,new TypeAnnotationsScanner()//设置类注解 扫描器 ,否则 getTypesAnnotatedWith 会报错
        )).getTypesAnnotatedWith(MyDubboRefrence.class);
        for (Class beanClazz : typesAnnotatedWith) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            //在这里，我们可以给该对象的属性注入对应的实例。
            //比如mybatis，就在这里注入了dataSource和sqlSessionFactory，
            // 注意，如果采用definition.getPropertyValues()方式的话，
            // 类似definition.getPropertyValues().add("interfaceType", beanClazz);
            // 则要求在FactoryBean（本应用中即ServiceFactory）提供setter方法，否则会注入失败
            // 如果采用definition.getConstructorArgumentValues()，
            // 则FactoryBean中需要提供包含该属性的构造方法，否则会注入失败

            String host = properties.getProperty("dubbo.redis.host");
            Integer port = Integer.valueOf(properties.getProperty("dubbo.redis.port"));
            String password = properties.getProperty("dubbo.redis.password");
            RedisDiscoveryCenter redisDiscoveryCenter = new RedisDiscoveryCenter();
            redisDiscoveryCenter.jedis(host,port,password);
            AsyncTaskExecutor executor = ExecutorServicePool.executor();
            definition.getPropertyValues().addPropertyValue("redisDiscoveryCenter", redisDiscoveryCenter);
            definition.getPropertyValues().addPropertyValue("executor", executor);
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);

            //注意，这里的BeanClass是生成Bean实例的工厂，不是Bean本身。
            // FactoryBean是一种特殊的Bean，其返回的对象不是指定类的一个实例，
            // 其返回的是该工厂Bean的getObject方法所返回的对象。
            definition.setBeanClass(ProxyFactory.class);

            //这里采用的是byType方式注入，类似的还有byName等
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(beanClazz.getSimpleName(), definition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}