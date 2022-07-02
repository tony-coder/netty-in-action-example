package com.nia.chapter4;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 不通过Netty使用OIO
 * 传统的阻塞式编程
 * <p>
 * 这段代码完全可以处理中等数量的并发客户端。但是随着应用程序变得流行起来，你会发现
 * 它并不能很好地伸缩到支撑成千上万的并发连入连接。你决定改用异步网络编程，但是很快就发
 * 现异步 API 是完全不同的，以至于现在你不得不重写你的应用程序。
 */
public class PlainOioServer {

    public static void main(String[] args) throws IOException {
        // 将服务器绑定到指定端口
        final ServerSocket socket = new ServerSocket(8080);
        System.out.println("服务器准备就绪");
        System.out.println("服务器信息：" + socket.getInetAddress() + ":" + socket.getLocalPort());
        try {
            for (; ; ) {
                final Socket clientSocket = socket.accept();
                System.out.println("Accept connection from " + clientSocket);
                // 创建一个新的线程来处理该连接
                new Thread(new Runnable() {
                    private boolean flag = true;

                    @Override
                    public void run() {
                        System.out.println("新客户端连接到了服务器：" + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                        try {
//                            OutputStream out = clientSocket.getOutputStream();

                            //打印流，用于服务器回送数据
                            PrintStream socketOutput = new PrintStream(clientSocket.getOutputStream());
                            //得到输入流，用于接收数据
                            BufferedReader socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                            //自旋，当收到客户端传来的"bye"字符串时才终止
                            while (flag) {
                                //接收客户端发来的一行requestBody
                                String str = socketInput.readLine();
                                System.out.println(str);
                                if (str != null) {
                                    //如果客户端发来的是"bye"，就把flag设置false，关闭资源
                                    if ("bye".equalsIgnoreCase(str)) {
                                        flag = false;
                                        socketOutput.println("客户端:" + clientSocket.getInetAddress() + "请求关闭连接");
                                    } else {
                                        //在控制台输出客户端发来的信息以及字符
                                        System.out.println("服务器接收到来自客户端" + clientSocket.getInetAddress() + "的信息:" + str);
                                        //返回客户端字符串的长度
                                        socketOutput.println(str.length());
                                    }
                                }
                            }
                            // 将消息写给已经联结的客户端
                            //out.write("Hi\r\n".getBytes(StandardCharsets.UTF_8));
                            //out.flush();
                            //关闭IO
                            socketInput.close();
                            socketOutput.close();
                            // 关闭连接
                            clientSocket.close();
                            System.out.println("关闭连接");
                        } catch (IOException e) {
                            System.out.println("连接异常");
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


}
