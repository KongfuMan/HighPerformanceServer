package SingleThread.HTTPServer;

import java.net.*;
import java.io.*;

// suppose
// only one thread running this Server application in the web server
public class Server implements Runnable
{
    private ServerSocket serverSocket;
    private Socket client = null;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run()
    {
        //线程不被打断，就持续接受链接请求
        while(!Thread.currentThread().isInterrupted())
        {
            try
            {
                System.out.println("wait for connection, port: " + serverSocket.getLocalPort());
                client = serverSocket.accept();
                System.out.println("accepted client host: " + client.getRemoteSocketAddress());
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();
                int len = 0; // 读取数据的长度
                byte[] dataBuffer = new byte[1024];
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while(!(line = reader.readLine()).isEmpty()){
                    System.out.println(line);
                }

                PrintWriter writer = new PrintWriter(out);
                InputStream fileIn = new FileInputStream("/Users/chenliang/Documents/GitHub/SocketProgramming/src/SingleThread/HTTPServer/index.html");
                BufferedReader htmlReader = new BufferedReader(new InputStreamReader(fileIn));
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: text/html;charset=utf-8");
                writer.println("Content-Length: "+fileIn.available());
                writer.println();   //header之后需要空一行
                writer.flush();
                String outLine = null;
                while((outLine = htmlReader.readLine())!=null){
                    writer.write(outLine);
                }
                writer.flush();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }catch(IOException e){
                e.printStackTrace();
            }finally {
                if (client != null){
                    try {
                        client.close();
                        client = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
    public static void main(String [] args) throws IOException {
        //用一个子线程去运行server
        int port = 30001;
        Thread t = new Thread(new Server(port));
        t.start();
    }
}