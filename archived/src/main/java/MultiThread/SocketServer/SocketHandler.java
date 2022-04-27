package MultiThread.SocketServer;

import java.io.*;
import java.net.Socket;

public  class SocketHandler implements Runnable{
    private Socket client;

    public SocketHandler(Socket socket) {
        client = socket;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try{
                InputStream input = client.getInputStream();
                byte[] buffer = new byte[1024];
                int len = input.read(buffer);
                if (len > 0){
                    System.out.println(Thread.currentThread().getName() + ": " + new String(buffer,0,len));
                }else{  // which means the client call close()
                    System.out.println("disconnected");
                    break;
                }
            }catch (Exception e) {
                break;
            }
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
