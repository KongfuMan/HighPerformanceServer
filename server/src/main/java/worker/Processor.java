package worker;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The processor thread that handles newly accepted new connections from main selector, including
 * 1. register the new connection for read operation;
 * 2. read request from client channel.
 * 3. write response into corresponding channel
 *
 * */
public class Processor implements Runnable {
    private ArrayBlockingQueue<SocketChannel> newConnections;
    private Map<String, SocketChannel> channelMap;
    private ArrayBlockingQueue<Object> responses;
    private int connectionQueueSize;
    private Selector selector;

    public Processor(int connectionQueueSize) throws IOException {
        this.newConnections = new ArrayBlockingQueue<SocketChannel>(connectionQueueSize);
        this.channelMap = new ConcurrentHashMap<>();
        this.connectionQueueSize = connectionQueueSize;
        this.selector = Selector.open();
    }

    @Override
    public void run() {
        try {
            configNewConnections();
            while(!Thread.interrupted()){
                processSelectedKeys();

            }
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register any new responses for writing
     *
     * */
    private void processNewResponses() throws IOException {
        int readyCnt = selector.select();
        if (readyCnt > 0){
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if (key.isWritable()){

                }
            }
        }
    }

    /**
     * Queue up the new connection channels for read
     * */
    public void accept(SocketChannel client) {
        if (client == null){
            return;
        }
        newConnections.offer(client);
    }

    /**
     * Register any new connections that have been queued up. The number of connections processed
     * in each iteration is limited to ensure that traffic and connection close notifications of
     * existing channels are handled promptly.
     */
    public void configNewConnections() throws IOException {
        int connectionsProcessed = 0;
        while (connectionsProcessed < connectionQueueSize && !newConnections.isEmpty()) {
            SocketChannel client = newConnections.poll();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            connectionsProcessed++;
        }
    }

    public void processSelectedKeys() throws IOException {
        int select = selector.select();
    }
}
