package org.xiaowu.mydubbo.framework.register;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiaowu.mydubbo.framework.protocol.Url;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 小五
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "dubbo.redis")
public class RedisRegisterCenter {

    private String host;

    private int port;

    private String password;

    @Bean
    public Jedis jedis(){
        Jedis jedis = new Jedis(host, port);
        if (StrUtil.isNotEmpty(password)){
            jedis.auth(password);
        }
        return jedis;
    }

    // 服务注册
    public void register(String interfaceName, Url url){
        try {
            Jedis jedis = jedis();
            String s = jedis.get(interfaceName);
            List<Url> urls = null;
            if (s == null){
                urls = new ArrayList<>();
            }else {
                JSONArray objects = JSONUtil.parseArray(s);
                urls = objects.toList(Url.class);
            }
            urls.add(url);
            jedis.set(interfaceName,JSONUtil.toJsonStr(urls));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
