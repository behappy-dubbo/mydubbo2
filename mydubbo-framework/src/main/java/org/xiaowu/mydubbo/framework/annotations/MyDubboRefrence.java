package org.xiaowu.mydubbo.framework.annotations;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author 小五
 * 标注在接口上,使客户端在调用之初将该接口动态代理到spring中
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)//VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Component
public @interface MyDubboRefrence {
}
