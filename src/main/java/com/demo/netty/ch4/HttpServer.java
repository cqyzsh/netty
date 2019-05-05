package com.demo.netty.ch4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * netty实现http服务端
 * 浏览器访问http://127.0.0.1:8088/，可看到响应结果
 */
public class HttpServer {

    public static void main(String[] args) throws Exception {

        // 定义一对线程组
        // 主线程组, 用于接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 从线程组, 处理具体的业务逻辑
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // netty服务器的创建, ServerBootstrap 是一个启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)	// 设置nio的双向通道
                    .childHandler(new HttpServerInitializer()); // 子处理器，用于处理workerGroup

            // 启动server，并且设置8088为启动的端口号，同时启动方式为同步
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();

            // 监听关闭的channel，设置位同步方式
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}