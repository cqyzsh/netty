package com.demo.netty.ch3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 单例的设计模式，直接提供INSTANCE给其他类调用
 */
@ChannelHandler.Sharable
public class ClientBusinessHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public static final ChannelHandler INSTANCE = new ClientBusinessHandler();

    private static AtomicLong beginTime = new AtomicLong(0);
    private static AtomicLong totalResponseTime = new AtomicLong(0);
    private static AtomicInteger totalRequest = new AtomicInteger(0);

    public static final Thread THREAD = new Thread(() -> {
        try {
            while (true) {
                // duration，为服务启动持续的时间
                long duration = System.currentTimeMillis() - beginTime.get();
                // totalRequest.get() / duration，每毫秒的连接数
                // ((float) totalResponseTime.get()) / totalRequest.get()，所有交易总的时间/总的请求数
                if (duration != 0) {
                    System.out.println("qps: " + 1000 * totalRequest.get() / duration + ", " + "avg response time: " + ((float) totalResponseTime.get()) / totalRequest.get());
                    Thread.sleep(2000);
                }
            }

        } catch (InterruptedException ignored) {
        }
    });

    /**
     * 每隔1秒向服务器端发送消息
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.executor().scheduleAtFixedRate(() -> {

            ByteBuf byteBuf = ctx.alloc().ioBuffer();
            byteBuf.writeLong(System.currentTimeMillis());
            ctx.channel().writeAndFlush(byteBuf);

        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 回调方法，接受服务器端的返回
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        // msg中保存的是客户端发起交易的时间
        // 接收到服务器端返回后的时间-交易发起时间=交易响应时间
        // 此处有一个累加的过程，所以totalResponseTime是所有请求的总时间
        totalResponseTime.addAndGet(System.currentTimeMillis() - msg.readLong());
        totalRequest.incrementAndGet();

        // 启动一个线程，用于统计一下qps
        // 在此处启动线程是保证已经接受到了服务器端的返回，totalRequest已经有值了，才能统计qps
        // 第一次接受到服务端消息后，赋值beginTime
        if (beginTime.compareAndSet(0, System.currentTimeMillis())) {
            THREAD.start();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // ignore
    }
}
