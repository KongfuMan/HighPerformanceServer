package NIO.SingleThreadNIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 *
 * */
public class Client {
    private static final int SIZE = (int)1e9;

    public static void main(String[] args) throws IOException {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress(30001));
        ByteBuffer writeBuf = Charset.defaultCharset().encode(Utility.generateLargeString(SIZE));
        while(writeBuf.hasRemaining()){
            int write = clientChannel.write(writeBuf);
            System.out.println("Write: " + write);
        }
        while(true){

        }
    }
}
