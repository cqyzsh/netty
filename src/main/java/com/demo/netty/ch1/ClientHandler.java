package com.demo.netty.ch1;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientHandler {

    public static final int MAX_DATA_LEN = 1024;
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * 为每一个业务逻辑处理，启动单独的线程进行处理
     */
    public void start() {
        System.out.println("新客户端接入");
        new Thread(new Runnable() {
            @Override
            public void run() {
                doStart();
            }
        }).start();
    }

    /**
     * 获取输入流，读取数据
     * 获取输出流，并将接受到的数据写回客户端
     */
    private void doStart() {
        try {
            InputStream inputStream = socket.getInputStream();
            while (true) {
                byte[] data = new byte[MAX_DATA_LEN];
                int len;
                while ((len = inputStream.read(data)) != -1) {
                    String message = new String(data, 0, len);
                    System.out.println("客户端传来消息: " + message);
                    socket.getOutputStream().write(data);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
