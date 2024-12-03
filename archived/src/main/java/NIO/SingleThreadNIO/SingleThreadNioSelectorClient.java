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
@Slf4j
public class SingleThreadNioSelectorClient {
    private static final int SIZE = (int) 1e7;
    private static Selector selector;
    private static SelectionKey selectionKey;
    private static ByteBuffer readBuf;
    private static SocketChannel clientChannel;

    public static void main(String[] args) throws IOException {
        selector = Selector.open();
        clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);

        // non-blocking initiate connect request to server
        // if return true, the connect is established immediately.
        boolean connected = clientChannel.connect(new InetSocketAddress(8082));
        if (!connected) {
            // if the connection is not done immediately, register into selector
            selectionKey = clientChannel.register(selector, SelectionKey.OP_CONNECT);
        }
        readBuf = ByteBuffer.allocate(1024);
        while (true) {
            // blocking if there is interested operations ready
            selector.selectNow();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
                processSelectionKey();
            }
        }
    }

    private static void processSelectionKey() throws IOException {
        if (selectionKey.isConnectable()) {
            try {
                // this method may be invoked to complete the connection sequence
                // 还记得server之中的我们讲过对于一个ready的channel, 要么处理，要么取消
                // 这个finishConnect()方法就是对connectable event的处理
                if (clientChannel.finishConnect()) {
                    // connection has established, register READ event
                    selectionKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                    selectionKey.interestOpsOr(SelectionKey.OP_READ);
                    log.info("Connection with server is done");
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Finishing connection with server failed");
            }
        } else if (selectionKey.isReadable()) {
            ByteBuffer buf = ByteBuffer.allocate(10);
            try {
                // return 0: 表示等待server发送数据过来，但是由于是非阻塞的，所以
                // 返回0.
                int read = clientChannel.read(buf);
                if (read < 0) {
                    // return -1: server主动的调用close()方法，此时channel仍然是可读的，
                    // 只不过读取的字节数为-1.
                    selectionKey.cancel();
                    clientChannel.close();
                    log.info("Server connection closed");
                    return;
                }
                buf.flip();
                log.info("Received message:  {}", Charset.defaultCharset().decode(buf).toString());
            } catch (Exception e) {
                selectionKey.cancel();
                clientChannel.close();
                log.error("Server connection lost exception");
            }
        } else if (selectionKey.isWritable()) {
            ByteBuffer writeBuf = (ByteBuffer) selectionKey.attachment();
            int write = clientChannel.write(writeBuf);
            log.info("{} bytes sent", write);
            if (!writeBuf.hasRemaining()) {
                selectionKey.attach(null); //GC the attachment
                selectionKey.interestOps(selectionKey.interestOps() & ~OP_WRITE);
                log.info("Write operation is done.");
            }
        }
    }

    private static void write() throws IOException {
        ByteBuffer writeBuf = Charset.defaultCharset().encode(Utility.generateLargeString(SIZE));
        int write = clientChannel.write(writeBuf);
        log.info("{} bytes sent", write);
        if (writeBuf.hasRemaining()) {
            selectionKey.interestOps(selectionKey.interestOps() | OP_WRITE);
            selectionKey.attach(writeBuf);
        }
    }
}
