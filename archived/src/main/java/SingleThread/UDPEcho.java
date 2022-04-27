package SingleThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//客服端想服务器发送一条消息，服务器打印并原封不动地发送给客户端
public class UDPEcho {
    public static void main(String[] args) throws IOException {
        DatagramSocket udp = new DatagramSocket(4888);

        while(true){
            byte[] buff = new byte[1024];
            DatagramPacket p = new DatagramPacket(buff,buff.length);

            //blocking here，等待发送到4888端口的消息，UDP是无连接协议
            udp.receive(p);
            System.out.println(p.getAddress());
            System.out.println(p.getPort());
            System.out.println(new String(p.getData(),0,p.getLength()));
            byte[] buffer = p.getData();
            DatagramPacket content = new DatagramPacket(buffer,0, buffer.length, p.getAddress(), p.getPort());
            udp.send(content);
        }
    }
}
