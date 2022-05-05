package NIO.SingleThreadNIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    private static final String msg = "aaaaaaa";

    public static void main(String[] args) throws IOException {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress(30001));
        ByteBuffer writeBuf = Charset.defaultCharset().encode(Utility.generateLargeString());
        clientChannel.write(writeBuf);
    }
}
