package NIO;

//  BIO handles the low concurrency, the disadvantage of BIO is that it does not
//  solve the blocking caused by internet IO or File IO, which means that
//  socket.recvmsg() will block

/*
*  The NIO means Non-blocking  IO
*  We use a selector to poll(轮询) the channel that register on it.
*  channel is used for bi-directional data transfer
*
* */
public class Server {
//    private final static int PORT = 30001;
//    private static ServerHandler serverHandler; //singleton
//
//    public static void startServer(){
//        if (serverHandler != null){
//            return;
//        }
//        synchronized (Server.class){
//            if (serverHandler == null){
//                serverHandler = new ServerHandler(PORT);
//            }
//        }
//        new Thread(serverHandler).start();
//
//    }
//
//    public static void main(String[] args) {
//        Server.startServer();
//    }

}
