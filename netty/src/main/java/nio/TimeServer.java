package nio;

import nio.MultiplexerTimeServer;

import java.io.IOException;

/**
 * @author: kevin.
 * @date: 2016/7/19.
 * @package: nio.
 * @version: 1.0.0.
 * @description:
 */
public class TimeServer {

    public static void main(String[] args) throws IOException{
        int port = 8082;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer,"nio-multiplexerTimeServer-001").start();

    }
}
