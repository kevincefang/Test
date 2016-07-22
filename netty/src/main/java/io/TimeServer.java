package io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: kevin.
 * @date: 2016/7/18.
 * @package: com.netty.
 * @version: 1.0.0.
 * @description:
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8081;
        if(args != null && args.length > 0 ) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //采用默认值
            }
        }
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("The time server is start in port :"+port);
                Socket socket = null;
                while(true){
                    socket = serverSocket.accept();
                    new Thread(new TimeServerHandler(socket)).start();
                }
            }finally {
                if(serverSocket != null){
                    System.out.println("The time server close");
                    serverSocket.close();
                    serverSocket = null;
                }
            }


        }



}
