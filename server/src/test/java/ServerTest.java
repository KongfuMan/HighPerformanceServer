import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    @Test
    public void test_server_accept_connections(){
        int port = 65423;
        SocketAddress addr = new InetSocketAddress(port);
        int backlog = 20;
        try {
            Server server = new Server(addr, backlog);
            server.start();
            Thread.sleep(200);
            for (int i = 0; i < 20; i++){
                SocketChannel client = SocketChannel.open();
                boolean connected = client.connect(addr);
                System.out.println("Client_" + i + "   " +  connected  + " portal: "+ client.getLocalAddress().toString());
            }
            while(true){

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}