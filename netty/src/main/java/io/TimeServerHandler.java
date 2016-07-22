package io;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * @author: kevin.
 * @date: 2016/7/18.
 * @package: PACKAGE_NAME.
 * @version: 1.0.0.
 * @description:
 */
public class TimeServerHandler implements Runnable {

    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(),true);
            String currentTime = null;
            String body = null;
            while(true){
                body = in.readLine();
                if(body == null){
                    System.out.println("The time server receive order:" + body);
                    currentTime = "query time order".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString():"bad order";
                    System.out.println(currentTime);
                    break;
                }
            }

        }catch(Exception e){
            if(in != null){
                try{
                    in.close();
                }catch(IOException e1){
                    e1.printStackTrace();
                }
            }
            if(out != null){
                out.close();
                out = null;
            }
            if(this.socket != null){
                try {
                    this.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                this.socket = null;
            }
        }
    }
}
