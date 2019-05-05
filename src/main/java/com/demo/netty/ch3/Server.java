package com.demo.netty.ch3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import static com.demo.netty.ch3.Constant.PORT;

/**
 * netty服务端
 * 1.直接在pipeline中添加BusinessHandler，业务逻辑会占用连接资源
 * 2.在pipeline中直接给BusinessHandler分配EventLoopGroup，所有的业务逻辑都是单独的线程
 * 3.在BusinessHandler中启动线程池处理，共通的业务逻辑可以放在线程池之外处理
 */
public class Server {
    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup businessGroup = new NioEventLoopGroup(1000);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);


        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new FixedLengthFrameDecoder(Long.BYTES));
//                ch.pipeline().addLast(ServerBusinessHandler.INSTANCE);
//                ch.pipeline().addLast(businessGroup, ServerBusinessHandler.INSTANCE);
                ch.pipeline().addLast(ServerBusinessThreadPoolHandler.INSTANCE);
            }
        });

        bootstrap.bind(PORT).addListener((ChannelFutureListener) future -> System.out.println("bind success in port: " + PORT));
    }
}
