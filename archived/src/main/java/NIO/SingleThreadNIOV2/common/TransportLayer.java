package NIO.SingleThreadNIOV2.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class TransportLayer {
    SocketChannel channel;
    SelectionKey key;

    public boolean finishConnect() throws IOException {
        boolean connected = this.channel.finishConnect();
        if (connected)
            key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
        return connected;
    }

    public int read(ByteBuffer buff) throws IOException {
        return this.channel.read(buff);
    }

    public int write(ByteBuffer buff) throws IOException {
        return this.channel.write(buff);
    }
}
