package MultiThread.SocketServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// multi thread with blocking io 
//create a new thread to handle each connection from client
//SocketHandler is the task that used for handling the communication with the client
public final class Server {
    private static ServerSocket serverSocket;   //singleton ServerSocket
    private final static int PORT = 30001;

    private static void start(int port)throws IOException{
        //singleton serverSocket
        if (serverSocket != null){
            return;
        }
        synchronized (Server.class){
            if (serverSocket == null){
                serverSocket = new ServerSocket(port);
            }
        }
        System.out.println("Server bind and listen to the port: " + port);
        while(true){
            Socket socket = serverSocket.accept();  //block here to wait for connection from client
            new Thread(new SocketHandler(socket)).start();
        }
    }

    public static void startServer()throws IOException{
        start(PORT);
    }

    public static void main(String[] args) {
        try {
            Server.startServer();
        } catch (IOException e) {
            System.exit(1);
        }
    }
}
