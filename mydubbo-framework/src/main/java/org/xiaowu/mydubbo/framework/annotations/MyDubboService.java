package org.xiaowu.mydubbo.framework.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author 小五
 * 标注在实现类上,使其被找到并注册到注册中心
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)//VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Component
public @interface MyDubboService {
    Class<?> value();
}
