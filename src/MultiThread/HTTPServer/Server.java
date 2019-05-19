package MultiThread.HTTPServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//multiThread  HTTPServer (modification to the single version)
public class Server
{
    public static void main(String [] args) throws IOException {
        int port = 30001;
        ServerSocket server = new ServerSocket(port);
        while(true){
            Socket client = server.accept();
            new Thread(new SocketHandler(client)).start();
        }
    }
}