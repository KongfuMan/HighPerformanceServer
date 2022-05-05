package my_netty.nio.channel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class NioServerSocketChannel extends AbstractNioChannel {
    private final List<Object> readBuf;

    public NioServerSocketChannel() {
        super(SelectionKey.OP_ACCEPT);
        this.readBuf = new ArrayList();
        try {
            this.channel = ServerSocketChannel.open();
            this.channel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read() {
        List<Object> readBuf = new ArrayList<>();
        boolean closed = false;
        try {
            do {
                int localRead = doReadMessages(readBuf);
                if (localRead == 0) {
                    break;
                }
                if (localRead < 0) {
                    closed = true;
                    break;
                }
            } while (true);
        } catch (Throwable t) {
            pipeline().fireExceptionCaught(t);
        }

        int size = readBuf.size();
        for (int i = 0; i < size; i ++) {
            pipeline().fireChannelRead(readBuf.get(i));
        }
        readBuf.clear();
        pipeline().fireChannelReadComplete();

        if (closed) {
            pipeline().fireChannelClosed();
        }
    }

    @Override
    public void write() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }

    /**The read operation for server socket is equivalent to accept operation*/
    protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = ((ServerSocketChannel)this.javaSocketChannel()).accept();
        try {
            if (ch != null) {
                buf.add(new NioSocketChannel(this, ch));
                return 1;
            }
        } catch (Throwable var6) {
            logger.warn("Failed to create a new channel from an accepted socket.", var6);
            try {
                ch.close();
            } catch (Throwable var5) {
                logger.warn("Failed to close a socket.", var5);
            }
        }
        return 0;
    }
}
