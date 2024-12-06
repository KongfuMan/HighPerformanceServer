package NIO.SingleThreadNIO;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Utility {

    static String readBufferToString(ByteBuffer buff) {
        return Charset.defaultCharset().decode(buff).toString();
    }

    public static String generateLargeString(){
        return generateLargeString(5000000);
    }

    public static String generateLargeString(int size){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++){
            sb.append('a');
        }
        return sb.toString();
    }
}
