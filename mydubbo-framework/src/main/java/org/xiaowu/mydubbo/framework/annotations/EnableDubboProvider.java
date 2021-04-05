package org.xiaowu.mydubbo.framework.annotations;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.xiaowu.mydubbo.framework.protocol.Url;
import org.xiaowu.mydubbo.framework.provider.NettyServer;
import org.xiaowu.mydubbo.framework.register.RedisRegisterCenter;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@EnableAsync
@Import({RedisRegisterCenter.class, Url.class,NettyServer.class})
public @interface EnableDubboProvider {
}
