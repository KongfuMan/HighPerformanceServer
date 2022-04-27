import org.apache.log4j.Logger;
import worker.WorkerSelector;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server extends Thread{
    private ServerSocketChannel serverChannel;
    private MainSelector selector;
    private int currentWorker;
    private List<WorkerSelector> workers;
    private static Logger log = Logger.getLogger(Server.class.getName());

    public Server(SocketAddress addr, int backlog) throws IOException {
        if (this.serverChannel.isOpen()){
            this.serverChannel.close();
        }
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(false);
        this.serverChannel.bind(addr, backlog);
        selector = new MainSelector();
        selector.register(serverChannel, SelectionKey.OP_ACCEPT);
        initializeWorkers();
    }

    private void initializeWorkers() throws IOException {
        workers = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            workers.add(new WorkerSelector(10));
        }
        currentWorker = 0;
    }

    public void start(){

    }

    @Override
    public void run() {
        while(!Thread.interrupted()){
            try {
                Iterator<SelectionKey> iterator = selector.select().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    WorkerSelector workerSelector = getNextWorker();
                    if (key.isAcceptable()) {
                        SocketChannel client = serverChannel.accept();
                        client.configureBlocking(false);
                        workerSelector.register(client, SelectionKey.OP_READ);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    private WorkerSelector getNextWorker(){
        return workers.get(currentWorker++);
    }


}
