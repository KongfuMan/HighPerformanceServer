package my_netty.nio.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NioSocketChannel extends AbstractNioChannel {

    private ByteBuffer byteBuffer;

    public NioSocketChannel() {
        this(null, null);
    }

    public NioSocketChannel(AbstractNioChannel parent, SocketChannel socket) {
        super(parent, SelectionKey.OP_READ);
        try {
            this.channel = socket == null ? SocketChannel.open() : socket;
            this.channel.configureBlocking(false);
            this.byteBuffer = ByteBuffer.allocate(1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read() {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        boolean isClosed = false;
        try {
            do{
                // read返回-1: client主动的调用close()方法，正常断开连接。
                // 所以在这种场景下，（服务器程序）你需要关闭socketChannel;
                //
                // read返回0: 有2种情况
                //  1. client 没有数据可读, 但没有调用close().
                //  2. bytebuffer position == limit, 不能存放更多数据了(可以避免）
                int bytes = ((SocketChannel)this.javaSocketChannel()).read(buf);
                if (bytes <= 0){
                    // 当本次读取的bytes个数为0 或 -1, 表示读取完成或者client关闭
                    isClosed = bytes < 0;
                    break;
                }
                this.pipeline().fireChannelRead(buf);
            }while(true);
            pipeline().fireChannelReadComplete();

            if (isClosed){
                //client call close() method
                pipeline().fireChannelClosed();
            }
        } catch (IOException e) {
            // client 非正常断开连接，而并非调用close()
            e.printStackTrace();
        }
    }

//    @Override
//    public void write(Object msg) {
//        ByteBuffer buf = Charset.defaultCharset().encode(msg.toString());
//        while(buf.hasRemaining()){
//            try {
//                // return 实际写到socketchannel中的字节数。
//                // 有可能值为0， 此时表示写缓冲区满了(OS内核缓冲区?)，无法写入，
//                // 由于是非阻塞的，所以此时返回0
//                // TODO: 应该避免返回值为0时去循环，浪费cpu时间。可以使用isWritable()方法。
//                // 确定可写的时候再执行write()方法。
//                int writes = ((SocketChannel) javaSocketChannel()).write(buf);
//                pipeline().fireChannelWrite();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    protected final void setOpWrite() {
        final SelectionKey key = selectionKey();
        // Check first if the key is still valid as it may be canceled as part of the deregistration
        // from the EventLoop
        // See https://github.com/netty/netty/issues/2104
        if (!key.isValid()) {
            return;
        }
        final int interestOps = key.interestOps();
        if ((interestOps & SelectionKey.OP_WRITE) == 0) {
            key.interestOps(interestOps | SelectionKey.OP_WRITE);
        }
    }

    protected final void clearOpWrite() {
        final SelectionKey key = selectionKey();
        // Check first if the key is still valid as it may be canceled as part of the deregistration
        // from the EventLoop
        // See https://github.com/netty/netty/issues/2104
        if (!key.isValid()) {
            return;
        }
        final int interestOps = key.interestOps();
        if ((interestOps & SelectionKey.OP_WRITE) != 0) {
            key.interestOps(interestOps & ~SelectionKey.OP_WRITE);
        }
    }

    ByteBuffer byteBuffer(){
        return this.byteBuffer;
    }


    /**
     * Write data into buffer. After call, the
     * data is still in the buffer and not flushed into
     * client.
     *
     * */
    @Override
    public void write() {

    }

    @Override
    public void flush() {
        ByteBuffer buf = byteBuffer();
        setOpWrite();
        try {
            int writes = ((SocketChannel) javaSocketChannel()).write(buf);
            pipeline().fireChannelWrite();

            if (buf.hasRemaining()){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
