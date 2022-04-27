package FakeBIO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int PORT = 30001;
    private static ServerSocket server = null;      //singleton
    private static ExecutorService service = Executors.newFixedThreadPool(50);


    public static void startServer() throws IOException{
        if (server != null){
            return;
        }
        synchronized (Server.class){
            if (server == null){
                server = new ServerSocket(PORT);
            }
        }

        while(true){
            Socket socket = server.accept();
            System.out.println("Server listen to the port: " + PORT);
            service.submit(new SocketHandler(socket));
        }
    }

    public static void main(String[] args) {
        try {
            Server.startServer();
        } catch (IOException e) {
            System.exit(1);
        }
    }

}
