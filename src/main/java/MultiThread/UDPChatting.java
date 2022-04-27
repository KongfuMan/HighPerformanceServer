package MultiThread;

import java.io.IOException;
import java.net.*;

// a multi thread chatting room by using udp
public class UDPChatting {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(4567, InetAddress.getByName("10.0.0.33"));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("input ip of your friend:");
        byte[] addr = new byte[1024];
        int addrlen = System.in.read(addr);
        String ipname = new String(addr,0,addrlen-1);
        System.out.println("input port of your friend:");
        byte[] port = new byte[1024];
        int portlen = System.in.read(port);
        Thread tr = new Thread(new receive(socket));
        Thread ts = new Thread(new send(socket,
                InetAddress.getByName(ipname),
                Integer.parseInt(new String(port,0,portlen-1))));
        tr.start();
        ts.start();
    }

    private static class receive implements Runnable{
        DatagramSocket socket = null;


        public receive(DatagramSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true){
                byte[] b = new byte[1024];
                DatagramPacket p = new DatagramPacket(b,b.length);
                try {
                    socket.receive(p);
                    System.out.println(Thread.currentThread().getName() + ":   "+ new String(p.getData(),0,p.getLength()));
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private static class send implements Runnable{
        DatagramSocket socket = null;
        InetAddress addr = null;
        int port = 0;

        public send(DatagramSocket socket, InetAddress addr, int port) {
            this.socket = socket;
            this.addr = addr;
            this.port = port;
        }

        @Override
        public void run() {
            while (true){
                System.out.println(Thread.currentThread().getName() + ". Input the message below:");
                byte[] b = new byte[1024];
                try {
                    int len = System.in.read(b);
                    DatagramPacket p = new DatagramPacket(b,len-1,addr,port);
                    socket.send(p);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }
}
