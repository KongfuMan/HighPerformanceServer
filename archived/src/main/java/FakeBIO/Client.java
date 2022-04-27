package FakeBIO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 30001;

    public static void send(){
        Socket socket = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            socket = new Socket(HOST,PORT);
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("Hello, I am Client. Host: " + socket.getLocalSocketAddress());
            in = new DataInputStream(socket.getInputStream());
            System.out.println(in.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client.send();
    }
}
