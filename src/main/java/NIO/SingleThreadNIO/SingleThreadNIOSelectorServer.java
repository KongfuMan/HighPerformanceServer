package NIO.SingleThreadNIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

// single thread non blocking socket by using selector
public class SingleThreadNIOSelectorServer {
    public static Selector selector;
    public static ServerSocketChannel server;
    public static ByteBuffer send;
    public static ByteBuffer receive;

    public static void main(String[] args) throws IOException {
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(4888));
        server.configureBlocking(false);

        // server can only accept
        selector = Selector.open();    //create a selector
        server.register(selector,SelectionKey.OP_ACCEPT);

        while(true){
            selector.selectNow();  //blocking here

            // get the selected keys of the channels that registered into this selector
            Set<SelectionKey> keys = selector.selectedKeys();

            // loop through all the interested selectionkeys (corresponding to a channel)
            Iterator<SelectionKey> iter = keys.iterator();
            while (iter.hasNext()){
                SelectionKey curKey = iter.next();
                iter.remove();
                keyHandler(curKey);
            }
        }

    }

    public static void keyHandler(SelectionKey key) throws IOException {
        if (key.isAcceptable()){
            // must be server channel
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
        if (key.isReadable()){
            // must be client channel
            SocketChannel client = (SocketChannel) key.channel();
            if (send == null){
                send = ByteBuffer.allocate(1024);
            }else {
                send.clear();
            }
            int len = client.read(send);
            System.out.println("read " + new String(send.array(),0,len));
            key.interestOps(SelectionKey.OP_WRITE);

        }
        if (key.isWritable()){
            SocketChannel client = (SocketChannel) key.channel();
            if (receive == null){
                receive = ByteBuffer.allocate(1024);
            }else {
                receive.clear();
            }
            client.write(receive);
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
