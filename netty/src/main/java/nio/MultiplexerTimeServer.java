package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: kevin.
 * @date: 2016/7/19.
 * @package: nio.
 * @version: 1.0.0.
 * @description:
 */
public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    /**
     * 初始化多路复用器、绑定监听端口
     * @param port
     */
    public MultiplexerTimeServer(int port){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);//设置成异步非阻塞模式
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);//绑定端口
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//将serverSocketChannel注册到Selector上
            System.out.println("The time server is start in port:"+port);
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void stop(){
        this.stop = true;
    }


    public void run() {

        while(!stop){
            try{
                selector.select(1000);//selector每隔1s都被唤醒一次
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                SelectionKey key = null;
                while(iterator.hasNext()){
                    key = iterator.next();
                    iterator.remove();
                    try{
                        handleInput(key);
                    }catch(Exception e){
                        if(key != null){
                            key.cancel();
                            if(key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }

            }catch(Throwable t){
                t.printStackTrace();
            }
        }
        if(selector != null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException{

        if(key.isValid()){
            //处理新接入的请求消息
            if(key.isAcceptable()){

                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);

                sc.register(selector,SelectionKey.OP_READ);
            }

            if(key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer); //读取到的字节数
                //字节数大于0时:对字节进行编解码
                if(readBytes > 0 ){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];//创建字节数组
                    readBuffer.get(bytes);//将缓冲区的可读字节复制到新的字节数组bytes中
                    String body = new String(bytes,"UTF-8");
                    System.out.println("The time server receive order:"+body);
                    String currentTime = "query time order".equalsIgnoreCase(body) ? new Date().toString() : "bad order";
                    //将缓冲区中的字节数组发送出去
                    doWrite(sc,currentTime);
                }
                //字节数小于0时:链路已经关闭，需要关闭SocketChannel，释放资源
                else if(readBytes < 0){
                    //对端链路关闭
                    key.cancel();
                    sc.close();
                }else{
                    ; //没有读到字节，属于正常场景，忽略;
                }

            }

        }

    }

    private void doWrite(SocketChannel channel,String response) throws IOException{

        if(response != null && response.trim().length() > 0 ){

            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);

        }


    }
}
