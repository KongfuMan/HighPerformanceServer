package BIO;

import java.io.*;
import java.net.Socket;

public class Client {
    private static int DEFAULT_SERVER_PORT = 30001;
    private static String DEFAULT_SERVER_IP = "127.0.0.1";

    public static void send(){
        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        try{
            socket = new Socket(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("Hello from " + socket.getLocalSocketAddress());
            in = new DataInputStream(socket.getInputStream());
            System.out.println("Server Respond: " + in.readUTF());
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
        }
    }

    public static void main(String[] args) {
        Client.send();
    }
}
