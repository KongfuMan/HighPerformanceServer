package NIO.SingleThreadNIO;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import static java.nio.channels.SelectionKey.OP_WRITE;

/**
 * Non-blocking TCP client that used to connect to server and read/write data.
 */
@Slf4j(topic = "SingleThreadNioSelectorClient")
public class SingleThreadNioSelectorClient {
    private static final int SIZE = (int) 1e7;
    private static final int EOF = -1;
    private static Selector selector;
    private static ByteBuffer readBuf;
    private static SocketChannel clientChannel;

    public static void main(String[] args) throws IOException {
        selector = Selector.open();
        clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);

        // tentatively initiate non-blocking connection to server
        // if return true, the connection is established immediately.
        boolean connected = clientChannel.connect(new InetSocketAddress(8082));
        if (!connected) {
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
        }
        readBuf = ByteBuffer.allocate(1024);
        while (true) {
            // blocking if there is interested operations ready
            selector.selectNow();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                processSelectionKey(selectionKey);
            }
        }
    }

    private static void processSelectionKey(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isConnectable()) {
            try {
                // this method may be invoked to complete the connection sequence
                // 还记得server之中的我们讲过对于一个ready的channel, 要么处理，要么取消
                // 这个finishConnect()方法就是对connectable event的处理
                if (clientChannel.finishConnect()) {
                    // connection has established, register READ event
                    selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
                    log.info("Connection with server is established");
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Finishing connection with server failed");
            }
        } 
        if (selectionKey.isReadable()) {
            ByteBuffer readBuff = ByteBuffer.allocate(10);
            try {
                int nRead = clientChannel.read(readBuff);
                if (nRead == EOF) {
                    // return -1: server主动的调用close()方法，此时channel仍然是可读的，
                    selectionKey.cancel();
                    clientChannel.close();
                    log.info("Server connection closed");
                    return;
                }
                readBuff.flip();
                log.info("Received message:  {}", Utility.readBufferToString(readBuff));
            } catch (Exception e) {
                selectionKey.cancel();
                clientChannel.close();
                log.error("Read from server failed.");
            }
        } 
        if (selectionKey.isWritable()) {
            ByteBuffer writeBuf = (ByteBuffer) selectionKey.attachment();
            int write = clientChannel.write(writeBuf);
            log.info("{} bytes sent", write);
            if (!writeBuf.hasRemaining()) {
                selectionKey.attach(null); // GC the attachment
                selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
                log.info("Write operation is done.");
            }
        }
    }
}