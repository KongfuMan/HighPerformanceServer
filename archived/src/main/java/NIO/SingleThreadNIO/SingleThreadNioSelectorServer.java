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
    private static int totalBytesRead = 0;


    public static void main(String[] args) throws IOException {
        // create a new selector instance.
        selector = Selector.open();

        // create server socket channel and set it as non-blocking
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);


        // Initially, serverSocketChannel is registered into selector
        // and ops = 0 表示对任何类型的events都不感兴趣
        serverSelectionKey = serverSocketChannel.register(selector, 0);

        // 表示selector关注了serverSocketChannel上发生的accept 事件
        serverSelectionKey.interestOps(OP_ACCEPT);

        serverSocketChannel.bind(new InetSocketAddress(PORT));
        while (true) {
            // blocking select() if no accept request from client.
            // 从OS内核地址空间把感兴趣的事件拷贝到JVM地址空间
            //
            // 注意：感兴趣的事件发生并被选择出来之后，要么处理、要么取消，这样才能从感兴趣集合中移除
            // 否则下次调用select()， 还会返回上次发生并未处理过的事件。
            // 例如： OP_ACCEPT发生之后，必须调用accept() or cancel()，否则下次select()还会有
            int selectedCount = selector.select();

            // 从JVM地址空间把感兴趣的事件拷贝到用户进程地址空间，注意
            // 这里的拷贝不是覆盖，而是append到用户空间，所以每次处理完
            // 一个key时，都需要手动remove()一下.
            Set<SelectionKey> keys = selector.selectedKeys();

            // loop through all the interested selection keys (corresponding to a channel)
            Iterator<SelectionKey> iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey curKey = iter.next();
                // refer to the comment above `Set<SelectionKey> keys = selector.selectedKeys();`
                iter.remove();
                processSelectionKey(curKey);
            }
        }

    }

    public static void processSelectionKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            // OP_ACCEPT事件一定发生在server socket channel之中
            ServerSocketChannel server = (ServerSocketChannel)key.channel();

            // 返回null, 对于non-blocking mode, 如果没有pending connections
            // 所以必须结合selector使用，当检测到acceptable的时候,可以返回client。
            SocketChannel client = server.accept();
            log.info("Client channel active");
            client.configureBlocking(false);
            SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
            write(clientKey, Utility.generateLargeString());
        }
        if (key.isReadable()) { //read from remote client
            // Associated channel must be a socket channel representing a client on server side
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer readBuf = ByteBuffer.allocate(1024 * 1024);
            try {
                // read() return -1: client主动的调用close()方法，正常断开连接。
                // 所以在这种场景下，(服务器程序)需要关闭socketChannel;
                //
                // read() return 0, 有2种情况:
                //  1. 当不用selector的时候，一般会用infinite loop read() 不断读取数据,
                //  如果client channel是non-blocking的话，此时不会等待，而是直接返回0.
                //  2. bytebuffer position == limit, 不能存放更多数据了(可以避免）
                int bytes = client.read(readBuf);
                totalBytesRead += bytes;
                if (bytes < 0) {
                    key.cancel();
                    client.close();
                    log.info("Client closed");
                    return;
                }
                readBuf.flip();
                log.info("{} bytes received, total: {} bytes ", bytes, totalBytesRead);
            } catch (Exception e) {
                key.cancel();
                client.close();
                log.error("Client connection lost unexpectedly");
            }
        }
        if (key.isWritable()) { // write into remote client
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer writeBuf = (ByteBuffer) key.attachment();
            int write = client.write(writeBuf);
            log.info("{} bytes was sent.", write);
            if (!writeBuf.hasRemaining()) {
                key.attach(null); //GC the attachment
                key.interestOps(key.interestOps() & ~OP_WRITE); //remove write ops from interest set.
                log.info("Write operation is done.");
            }
        }
    }

    /**
     * 写入较大数据时，可能需要分多次写，每次写入都需要调用OS的socket来写入，需要一定的IO时间
     * 而client channel被设置为non-blocking, 所以它不会阻塞等待底层写入，而是返回0，表示此次
     * 调用没有写入数据，这样会消耗CPU时间；
     * 正确的做法如下：写一次发现没写完，就向client channel的interest operation set中添加
     * OP_WRITE, 那么selector就会关注该channel的isWritable时间，等下次channel is ready
     * for write的时候再调用channel.write(buf)方法，确保此次可以写入时再写入，从而避免了
     * 浪费cpu时间
     */
    private static void write(SelectionKey key, Object msg) throws IOException {
        ByteBuffer writeBuf = Charset.defaultCharset().encode(msg.toString());
        SocketChannel client = (SocketChannel) key.channel();
        log.info("Initiate writing data to client");
        int write = client.write(writeBuf);
        log.info("{} bytes was sent.", write);
        if (writeBuf.hasRemaining()) {
            // 这里同时关注读取client 和 写入client
            // Liang: 应为TCP是全双工通信（duplex), 所以数据可以同时进行双向传输
            // 所以必须同时监听客户端的读写事件。
            key.interestOps(key.interestOps() | OP_WRITE);
            key.attach(writeBuf);
        }
    }
}
