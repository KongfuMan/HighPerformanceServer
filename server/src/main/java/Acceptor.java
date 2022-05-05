import org.apache.log4j.Logger;
import worker.Processor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * This is the equivalent to Netty `NioEventLoopGroup`, except that
 * this is the single threaded version.
 * */
public class Acceptor implements Runnable {
    /**
     * Leverage java nio selector.
     * Multi-threaded version can be used here.
     * We can encapsulate it to provide .
     * */
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private int currentWorker;
    private List<Processor> processors;
    private static Logger log = Logger.getLogger(Acceptor.class.getName());
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    public Acceptor(SocketAddress addr, int backlog) throws IOException {
        this.selector = Selector.open();
        if (this.serverChannel != null && this.serverChannel.isOpen()) {
            this.serverChannel.close();
        }
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(false);
        this.serverChannel.bind(addr, backlog);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        initializeWorkers();
    }

    private void initializeWorkers() throws IOException {
        processors = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            processors.add(new Processor(10));
        }
        currentWorker = 0;
    }

    /**
     * Select the channels ready for registered operations.
     * This method will block if NO channel is ready
     */
    @Override
    public void run() {
        while (isRunning.get()) {
            try {
                int count = selector.select(100);
                if (count <= 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel client = serverChannel.accept();
                        Processor processor = getNextProcessor();
                        processor.accept(client);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    private Processor getNextProcessor() {
        return processors.get((currentWorker++) % processors.size());
    }

    public void startProcessors() {
        for (Processor processor : processors) {
            (new Thread(processor)).start();
        }
    }
}
