package com.demo.netty.ch1;

import java.io.IOException;
import java.net.Socket;

public class Client {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;
    private static final int SLEEP_TIME = 5000;

    /**
     * 创建客户端Socket连接
     * 创建单独的线程，每隔5秒钟往服务端发送一条消息
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final Socket socket = new Socket(HOST, PORT);

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("客户端启动成功!");
                while (true) {
                    try {
                        String message = "hello world";
                        System.out.println("客户端发送数据: " + message);
                        socket.getOutputStream().write(message.getBytes());
                    } catch (Exception e) {
                        // 当服务端断开连接会报错
                        // java.net.SocketException: Connection reset by peer: socket write error
                        e.printStackTrace();
                        System.out.println("写数据出错!");
                    }
                    sleep();
                }
            }
        }).start();

    }

    private static void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
