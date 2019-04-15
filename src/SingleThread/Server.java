package SingleThread;

import java.net.*;
import java.io.*;

// suppose
// only one thread running this Server application in the web server
public class Server implements Runnable
{
    private ServerSocket serverSocket;

    public Server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000000);
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
                Socket server = serverSocket.accept();
                System.out.println("client host: " + server.getRemoteSocketAddress());
                InputStream in = server.getInputStream();
                while (in.re)
                System.out.println();
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF("Thanks for connection" + server.getLocalSocketAddress() + "\nGoodbye!");
                server.close();
            }catch(SocketTimeoutException s)
            {
                System.out.println("Socket timed out!");
                break;
            }catch(IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }
    public static void main(String [] args)
    {
        int port = 30001;
        try
        {
            Thread t = new Thread(new Server(port));
            t.start();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}