package examples;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelDemo {
    public static final String CONTENT = "This is a channel demo";
    public static void main(String[] args) throws IOException {
        String path = "server/src/main/resources/file_channel.txt";
        FileOutputStream fs = new FileOutputStream(path);
        FileChannel fileChannel = fs.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(CONTENT.getBytes());
        buffer.flip();
        fileChannel.write(buffer);
        fileChannel.close();
        fs.close();
    }
}
