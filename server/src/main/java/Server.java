import org.apache.log4j.Logger;
import worker.Processor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Server class represents a server.
 *
 * */
public class Server{
    private Acceptor acceptor;
    private static Logger log = Logger.getLogger(Server.class.getName());

    //TODO: Should be a singleton.
    public Server(SocketAddress addr, int backlog) throws IOException {
        acceptor = new Acceptor(addr, backlog);
    }

    public void start(){
        startAcceptorAndProcessors();
    }

    private void startAcceptorAndProcessors(){
        (new Thread(acceptor)).start();
//        acceptor.startProcessors();
    }
}
