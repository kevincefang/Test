package nio;

/**
 * @author: kevin.
 * @date: 2016/7/18.
 * @package: com.netty.
 * @version: 1.0.0.
 * @description:
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8082;
        new Thread(new TimeClientHandle("127.0.0.1",port),"timeClient-001").start();
    }
}
