package worker;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class WorkerSelector implements Runnable {
    private ArrayBlockingQueue<SocketChannel> que;
    private Selector selector;

    public WorkerSelector(int capacity) throws IOException {
        this.que = new ArrayBlockingQueue<SocketChannel>(capacity);
        this.selector = Selector.open();
    }

    public void addChannel(SocketChannel channel){
        que.add(channel);
    }

    @Override
    public void run() {
        while(!Thread.interrupted()){
            SocketChannel client = que.poll();
            try {
                register(client, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }

    public void register(SocketChannel client, int ops) throws ClosedChannelException {
        client.register(selector, ops);
    }
}
