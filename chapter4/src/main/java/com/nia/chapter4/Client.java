package com.nia.chapter4;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {

        //定于客户端socket
        Socket socket = new Socket();
        //设置连接超时时间为3000ms
        socket.setSoTimeout(3000);
        //连接本地服务器，端口号为2000，超时时间为3000
        socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8080), 3000);

        System.out.println("已经发起服务器连接");
        System.out.println("客户端Ip:" + socket.getLocalAddress() + "端口:" + socket.getLocalPort());
        System.out.println("服务器Ip:" + socket.getInetAddress() + "端口" + socket.getPort());

        try {
            send(socket);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("操作出错");
        }
        socket.close();
        System.out.println("客户端终止连接");
    }

    private static void send(Socket client) throws IOException {
        //键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //socket输出流
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        //获取socket输入流
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferReader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;

        while (flag) {
            //键盘读取一行
            String str = input.readLine();
            //发送到服务器
            socketPrintStream.println(str);

            //从服务器读取一行
            String echo = socketBufferReader.readLine();
            if (echo.equalsIgnoreCase("bye"))
                flag = false;
            else
                System.out.println(echo);

        }
    }

}
