package NIO.SingleThreadNIO;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;


class SingleThreadNIOSelectorServerTest {
    private static final String msg = "aaaaaaa";

    @Test
    public void client() throws IOException {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress(30001));
        clientChannel.close();
//        ByteBuffer writeBuf = Charset.defaultCharset().encode(msg);
//        clientChannel.write(writeBuf);
    }
}