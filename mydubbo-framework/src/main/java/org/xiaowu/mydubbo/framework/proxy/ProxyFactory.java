package org.xiaowu.mydubbo.framework.proxy;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.xiaowu.mydubbo.framework.discovery.RedisDiscoveryCenter;
import org.xiaowu.mydubbo.framework.protocol.Invocation;
import org.xiaowu.mydubbo.framework.register.RedisRegisterCenter;
import org.xiaowu.mydubbo.framework.protocol.Url;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.xiaowu.mydubbo.framework.consumer.NettyClient.clientHandler;
import static org.xiaowu.mydubbo.framework.consumer.NettyClient.initClient;

/**
 * @author 小五
 */
@RequiredArgsConstructor
public class ProxyFactory<T> implements FactoryBean<T> {

    @Setter
    private AsyncTaskExecutor executor;

    @Setter
    private RedisDiscoveryCenter redisDiscoveryCenter;

    private Class<T> interfaceClass;

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invocation = new Invocation(interfaceClass.getName(), method.getName(), method.getParameterTypes(), args);
                if(clientHandler == null){
                    Url url = redisDiscoveryCenter.get(interfaceClass.getName());
                    initClient(url.getHost(),url.getPort());
                }
                clientHandler.setInvocation(invocation);
                return executor.submit(clientHandler).get();
            }
        });
    }

    public ProxyFactory(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}