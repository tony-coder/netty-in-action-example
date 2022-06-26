package com.nia.chapter4;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 不通过Netty使用OIO
 * 传统的阻塞式编程
 * <p>
 * 这段代码完全可以处理中等数量的并发客户端。但是随着应用程序变得流行起来，你会发现
 * 它并不能很好地伸缩到支撑成千上万的并发连入连接。你决定改用异步网络编程，但是很快就发
 * 现异步 API 是完全不同的，以至于现在你不得不重写你的应用程序。
 */
public class PlainOioServer {
    public void server(int port) throws IOException {
        System.out.println("开始监听");
        // 将服务器绑定到指定端口
        final ServerSocket socket = new ServerSocket(port);
        try {
            for (; ; ) {
                final Socket clientSocket = socket.accept();
                System.out.println("Accept connection from " + clientSocket);
                // 创建一个新的线程来处理该连接
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out = null;
                        try {
                            out = clientSocket.getOutputStream();
                            // 将消息写给已经联结的客户端
                            out.write("Hi\r\n".getBytes(StandardCharsets.UTF_8));
                            out.flush();
                            // 关闭连接
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                // ignore on close
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();  // 启动线程
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        PlainOioServer server = new PlainOioServer();
        server.server(8080);
    }
}
