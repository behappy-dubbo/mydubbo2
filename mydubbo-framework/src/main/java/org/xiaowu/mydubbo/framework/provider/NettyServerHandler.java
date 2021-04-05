package org.xiaowu.mydubbo.framework.provider;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.xiaowu.mydubbo.framework.protocol.Invocation;

import java.lang.reflect.Method;
import java.util.Map;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Map<String, Class> handlerMap;

    public NettyServerHandler(Map<String, Class> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 接收到请求，处理请求
        Invocation invocation = (Invocation) msg;
        Class aClass = handlerMap.get(invocation.getInterfaceName());
        // 利用反射执行方法得到res
        Method method = aClass.getMethod(invocation.getMethodName(), invocation.getParamTypes());
        Object res = method.invoke(aClass.newInstance(), invocation.getParams());
        // 写回netty，让client端监听到
        ctx.writeAndFlush(res);
    }
}