package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;

/*
* use SocketChannel listen to the port
* user only selector in only one thread to poll all the channels that register on it
* the selector will keep polling all the channels and pick the channels that are ready
* to read and write
*
* */
public class ServerHandler implements Runnable {
    private static Selector selector;
    private static ServerSocketChannel socketChannel;

    public ServerHandler(int port) {
        try {
            selector = Selector.open(); //open selector
            socketChannel = ServerSocketChannel.open(); //open SeverSocketChannel
            socketChannel.configureBlocking(false); // set non-blocking mode
            socketChannel.socket().bind(new InetSocketAddress(port), 1024);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            socketChannel.register(selector, SelectionKey.OP_READ);
            socketChannel.register(selector, SelectionKey.OP_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                selector.select(1000);
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()){
                    SelectionKey key = it.next();
                    it.remove();
                    try {
                        handleSelectionKey(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }




            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void handleSelectionKey(SelectionKey key) throws IOException{
        if (key.isValid()){

        }
    }
}
