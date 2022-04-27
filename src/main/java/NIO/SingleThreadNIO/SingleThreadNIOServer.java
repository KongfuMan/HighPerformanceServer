package NIO.SingleThreadNIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

//single thread non blocking socket without using selector
public class SingleThreadNIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(30001));

        //used to store the client channels
        List<SocketChannel> clients = new ArrayList<SocketChannel>();
        while (!Thread.currentThread().isInterrupted()){

            //not blocking here
            SocketChannel clientChannel = serverChannel.accept();
            if (clientChannel != null){
                clientChannel.configureBlocking(false);
                clients.add(clientChannel);
                System.out.println(clientChannel.getLocalAddress() + " connected!");
            }

            // loop through the client array to read the incoming data
            for (int i = 0; i < clients.size(); i++){
                try{
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    long len = clients.get(i).read(buffer); // null pointer exception
                    if (len > 0){
                        System.out.println(new String(buffer.array(),0,(int)len));
                    }else if (len == 0) {   //wait client to send message
//                        System.out.println("waiting for message from client");
                    }else{
                        System.out.println("client close");
                        clients.remove(i);
                        i--;
                    }
                }catch (IOException e){
                    continue;
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

        }
    }
}
