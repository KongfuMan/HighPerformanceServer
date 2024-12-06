package NIO.SingleThreadNIO;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_WRITE;

/**
 * Single threaded non-blocking TCP server by using selector
 */
@Slf4j
public class SingleThreadNioSelectorServer {
    private final static int PORT = 30001;

    private static Selector selector;
    private static ServerSocketChannel serverSocketChannel;
    private static SelectionKey serverSelectionKey;

    public static void main(String[] args) throws IOException {
        // create a new selector instance.
        selector = Selector.open();

        // create server socket channel and set it as non-blocking
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        // Initially, serverSocketChannel is registered into selector
        // and ops = 0 indicates server is not interested in any ops.
        serverSelectionKey = serverSocketChannel.register(selector, 0);

        // interested in accept new connection, which only valid on server side.
        serverSelectionKey.interestOps(OP_ACCEPT);

        serverSocketChannel.bind(new InetSocketAddress(PORT));
        while (true) {
            // non-blocking select()
            int selectedCount = selector.selectNow();

            // 从JVM地址空间把感兴趣的事件拷贝到用户进程地址空间，注意
            // 这里的拷贝不是覆盖，而是append到用户空间，所以每次处理完
            // 一个key时，都需要手动remove()一下.
            Set<SelectionKey> keys = selector.selectedKeys();

            // loop through all the interested selection keys (corresponding to a channel)
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey curKey = iterator.next();
                iterator.remove();
                processSelectionKey(curKey);
            }
        }
    }

    public static void processSelectionKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel)key.channel();
            SocketChannel client = server.accept();
            log.info("Accepted client: {}", client.getRemoteAddress());
            client.configureBlocking(false);
            SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
        }
        if (key.isReadable()) {
            // Associated channel must be a socket channel representing a client on server side
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer readBuff = ByteBuffer.allocate(1024 * 1024);
            try {
                int nRead = channel.read(readBuff);
                if (nRead < 0) {
                    // remote client close connection gracefully, so dispose the resources accordingly.
                    key.cancel();
                    channel.close();
                    log.info("Client closed");
                    return;
                }
                readBuff.flip(); // prepare to read by application.
                log.info("Received client ({}) message: {}", channel.getRemoteAddress(), Utility.readBufferToString(readBuff));
            } catch (Exception e) {
                key.cancel();
                channel.close();
                log.error("Read from client failed.");
            }
        }
        if (key.isWritable()) { // write into remote client
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer writeBuf = (ByteBuffer) key.attachment();
            client.write(writeBuf);
            /**
             * 写入较大数据时，可能需要分多次写，每次异步写入不会阻塞等待，而是返回此次调用实际写入的size.
             * size >= 0 && size <= actual size need to be written. 每次调用write之后应该检查是否
             * 还有数据需要写入，如果没有就取消OP_WRITE. 否则继续保持订阅OP_WRITE，等待下次select时候继续write.
             */
            if (!writeBuf.hasRemaining()) {
                key.attach(null); //GC the attachment
                key.interestOps(key.interestOps() & ~OP_WRITE); //remove write ops from interest set.
                log.info("Write is done, de-register OP_WRITE");
            }
        }
    }

    static String readBufferToString(ByteBuffer buff) {
        return Charset.defaultCharset().decode(buff).toString();
    }
}
