package com.demo.netty.ch2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import static com.demo.netty.ch2.Constant.BEGIN_PORT;
import static com.demo.netty.ch2.Constant.N_PORT;

/**
 * 模拟单机百万连接
 * 单机百万连接指的是可以同时接受百万的请求，请求没有断开，并不是指的TPS
 * 服务器端启动100个端口进行监听
 * 用@Sharable注解，所用监听端口共用一个handler
 * 客户端循环向100个端口请求连接
 * 打包成jar放到虚拟机中运行
 * java -jar server.jar -Xms6.5g -Xmx6.5g -XX:NewSize=5.5g -XX:MaxNewSize=5.5g -XX:MaxDirectMemorySize=1g
 * java -jar client.jar -Xms6.5g -Xmx6.5g -XX:NewSize=5.5g -XX:MaxNewSize=5.5g -XX:MaxDirectMemorySize=1g
 * 局部文件句柄限制：cat /etc/security/limits.conf
 * *               soft    nofile           1000000
 * *               hard    nofile           1000000
 * 全局文件句柄限制：cat /proc/sys/fs/file-max
 */
public class Server {

    public static void main(String[] args) {
        new Server().start(BEGIN_PORT, N_PORT);
    }

    public void start(int beginPort, int nPort) {
        System.out.println("server starting....");

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childHandler(new ConnectionCountHandler());

        /**
         * 启动8000到8100共100端口的监听
         */
        for (int i = 0; i < nPort; i++) {
            int port = beginPort + i;
            bootstrap.bind(port).addListener((ChannelFutureListener) future -> {
                System.out.println("bind success in port: " + port);
            });
        }

        System.out.println("server started!");
    }
}
