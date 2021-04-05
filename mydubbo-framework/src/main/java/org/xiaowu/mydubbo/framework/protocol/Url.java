package org.xiaowu.mydubbo.framework.protocol;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "dubbo.server")
public class Url implements Serializable {

    private static final long serialVersionUID = 3574284138135147490L;

    String host;

    Integer port;
}
