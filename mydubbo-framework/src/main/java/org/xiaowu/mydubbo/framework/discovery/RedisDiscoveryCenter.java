package org.xiaowu.mydubbo.framework.discovery;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.xiaowu.mydubbo.framework.protocol.Url;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RedisDiscoveryCenter {

    private Jedis jedis;

    // random
    // 服务发现
    public Url get(String interfaceName) throws Exception{
        String s = jedis.get(interfaceName);
        JSONArray objects = JSONUtil.parseArray(s);
        List<Url> urls = objects.toList(Url.class);
        return urls.get(ThreadLocalRandom.current().nextInt(urls.size()));
    }

    public void jedis(String host, Integer port, String password){
        Jedis jedis = new Jedis(host, port);
        if (StrUtil.isNotEmpty(password)){
            jedis.auth(password);
        }
        this.jedis = jedis;
    }
}
