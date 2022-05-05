package examples;

import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * This demo shows how to use java.nio.Buffer
 * */
public class BufferDemo {
    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(5);
        for (int i = 0; i < intBuffer.capacity() / 2; i++){
            intBuffer.put(i * 2);
        }
        intBuffer.flip();
        int i = 1;
    }
}
