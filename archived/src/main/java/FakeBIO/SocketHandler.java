package FakeBIO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketHandler implements Runnable {
    private Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        DataInputStream in = null;
        DataOutputStream out = null;

        while (true){
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                try {
                    Thread.sleep(5000); //used for mock the delay of internet
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(in.readUTF());
                out.writeUTF("This is Server. I get your message");
            } catch (IOException e) {
                break;
            }
        }
    }
}
