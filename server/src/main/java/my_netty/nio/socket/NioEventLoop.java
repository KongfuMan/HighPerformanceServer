package my_netty.nio.socket;

import my_netty.nio.channel.AbstractNioChannel;
import my_netty.nio.channel.ChannelHandler;
import my_netty.nio.channel.ChannelHandlerContext;
import my_netty.nio.channel.DefaultChannelPipeline;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * The Single threaded wrapper of java nio selector. <br>
 *
 * Note: {@link ChannelHandlerContext}, {@link DefaultChannelPipeline}，
 * {@link AbstractNioChannel},{@link NioEventLoop},
 * {@link NioEventLoopGroup}, {@link ChannelHandler}是netty的核心类.
 * 它们不断循环，负责在合适的timing去trigger对应的event, 包括出现异常等等。
 * 其中{@link ChannelHandler}负责几乎全部的具体工作，包括channel的bind, connect,
 * read/accept, write and flush等.
 * 由于pipeline中的所有hook methods都是同步执行的，所以很重要的一点就是：
 * 不能在里面添加耗时任务(computation/IO intensive task)，会降低框架并发处理效率。
 * 如果需要添加这样的任务，必须采用采用异步处理。
 *
 */
public final class NioEventLoop implements Runnable {
    private final Selector selector;

    // TODO: optimize IO using the asynchronous task queue
    /**
     * 这个队列存放一些比较耗时的IO操作, 比如channelRead()。
     * 为了防止IO阻塞降低loop的并发处理能力，可以将任务提交
     * 给线程池进行异步执行。
     * 这里的 taskQue起到一个缓冲队列的作用
     */
    private Queue<Runnable> taskQue;
    private ExecutorService executor;

    public NioEventLoop() throws IOException {
        selector = Selector.open();
//        taskQue = new LinkedBlockingQueue<>();
//        executor = new ThreadPoolExecutor();
    }

    public Selector selector() {
        return selector;
    }

    /**
     * Register a {@link AbstractNioChannel} onto this event loop.
     * */
    public void register(AbstractNioChannel channel, int ops){
        register(channel, ops, null);
    }

    private void register(AbstractNioChannel channel, int ops, Object att){
        channel.register(ops);
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                processSelectedKeys();
            } finally {
                runAllTasks();
            }
        }
    }

    private void processSelectedKeys() {
        Set<SelectionKey> keys = selector.selectedKeys(); //选择出了ready channel
        Iterator<SelectionKey> iterator = keys.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            final Object a = key.attachment();
            if (a instanceof AbstractNioChannel) {
                processSelectedKey(key, (AbstractNioChannel) a);
            }
        }
    }

    private void processSelectedKey(SelectionKey key, AbstractNioChannel ch) {
        try {
            int readyOps = key.readyOps();
            if (key.isConnectable()) {
                int ops = key.interestOps();
                ops &= -9;
                key.interestOps(ops);
                ch.finishConnect();
            }

            if (key.isWritable()) {
                ch.write();
            }

            // Not differentiate accept and read, since we
            // abstract the accept event for server channel as
            // the read events, which can be handled by same api as client socket channel
            if (key.isAcceptable() || key.isReadable()) {
                ch.read();
            }

            if (readyOps == 0) {
                ch.read();
            }
        } catch (CancelledKeyException var7) {
            ch.close();
        }
    }

    private void runAllTasks() {

    }

}
