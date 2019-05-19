package MultiThread.HTTPServer;

import java.io.*;
import java.net.Socket;

public class SocketHandler implements Runnable {
    private Socket client = null;

    public SocketHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //短连接
        try
        {
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
            InputStream fileIn = new FileInputStream("/Users/chenliang/Documents/GitHub/SocketProgramming/src/MultiThread/HTTPServer/index.html");
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
            //模拟io，这里如果用多线程的话，会导致大量的线程IO等待。这就是我我们要用非阻塞IO
            Thread.sleep(10000);
        }catch(IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (client != null){
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client = null;
            }
        }
    }
}
