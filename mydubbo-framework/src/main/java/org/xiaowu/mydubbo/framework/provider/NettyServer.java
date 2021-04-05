package org.xiaowu.mydubbo.framework.provider;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.xiaowu.mydubbo.framework.annotations.MyDubboService;
import org.xiaowu.mydubbo.framework.protocol.Url;
import org.xiaowu.mydubbo.framework.register.RedisRegisterCenter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 小五
 */
@Slf4j
@Component
public class NettyServer implements ApplicationContextAware, CommandLineRunner {

    private RedisRegisterCenter redisRegisterCenter;

    private Url url;

    //用于存储业务接口和实现类的实例对象
    private Map<String, Class> handlerMap = new HashMap<>();

    // spring构造对象时会调用setApplicationContext方法，从而可以在方法中通过自定义注解获得用户的业务接口和实现
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        redisRegisterCenter = applicationContext.getBean(RedisRegisterCenter.class);
        url = applicationContext.getBean(Url.class);
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(MyDubboService.class);
        if (CollectionUtil.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                //从业务实现类上的自定义注解中获取到value，从来获取到业务接口的全名
                String interfaceName = serviceBean.getClass()
                        .getAnnotation(MyDubboService.class).value().getName();
                handlerMap.put(interfaceName, serviceBean.getClass());
            }
        }
    }

    /**
     * 组件启动时会执行run,启动netty服务
     * @throws Exception
     */
    @Async
    @Override
    public void run(String... args)  {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel)
                                throws Exception {
                            channel.pipeline()
                                    .addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                                    .addLast(new ObjectEncoder())
                                    .addLast(new NettyServerHandler(handlerMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            ChannelFuture future = bootstrap.bind(url.getHost(), url.getPort()).sync();
            for (String clazzName : handlerMap.keySet()) {
                redisRegisterCenter.register(clazzName,url);
            }
            log.info("mydubbo提供方启动");
            future.channel().closeFuture().sync();
        } catch (Exception e){
            log.error("mydubbo提供方启动error: {}",e.getMessage());
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


}
