package org.xiaowu.mydubbo.framework.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.xiaowu.mydubbo.framework.protocol.Invocation;

import java.util.concurrent.Callable;

/**
 * 因为调用的时候，需要等待调用结果，再将结果返回，这需要一个过程，所以需要用到线程等待 wait notify方法
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;

    private Invocation invocation;

    private String result;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //在run方法中会用得到
        this.context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg.toString();
        notify();
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(invocation);
        wait(10000);
        return result;
    }
}
