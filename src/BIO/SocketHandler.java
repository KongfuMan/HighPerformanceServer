package BIO;

import java.io.*;
import java.net.Socket;

public  class SocketHandler implements Runnable{
    private Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while(true){
            try{
                DataInputStream in = new DataInputStream(socket.getInputStream());
                System.out.println(in.readUTF());
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("Thanks for connection" + socket.getLocalSocketAddress() + "\nGoodbye!");
            }catch (Exception e) {
                continue;
            }
        }
    }
}
