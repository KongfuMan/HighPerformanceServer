package SingleThread;

import java.net.*;
import java.io.*;

public class Client
{
    public static void main(String [] args)
    {
        String serverName = "localhost";
        int port = 30001;
        try
        {
            System.out.println("connect to server: " + serverName + ", port: " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("server host: " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("Hello from " + client.getLocalSocketAddress());
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server Respond: " + in.readUTF());
            client.close();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}