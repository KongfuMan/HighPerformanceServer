package SingleThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//Single Thread blocking socket
public class TCPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(4888);

        //其他客户端必须等待当前客户端完成通信之后才能与服务器通信
        // 但是多个客户端仍然可以和服务器同时连接（为什么？）
        while (true){
            //block here until a client connect with server
            Socket client = server.accept();

            while (true){
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();
                byte[] buffer = new byte[1024];
                int len = in.read(buffer);
                if (len > 0){
                    System.out.println(new String(buffer,0,len));
                }else{  // which means the client call close()
                    break;
                }
            }
            client.close();
        }
    }
}
