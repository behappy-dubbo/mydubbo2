package org.xiaowu.mydubbo.framework.annotations;

import org.springframework.context.annotation.Import;
import org.xiaowu.mydubbo.framework.provider.NettyServer;
import org.xiaowu.mydubbo.framework.proxy.ServiceBeanDefinitionRegistry;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Import(ServiceBeanDefinitionRegistry.class)
public @interface EnableDubboConsumer {
}
