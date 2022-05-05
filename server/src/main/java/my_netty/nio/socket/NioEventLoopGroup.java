package my_netty.nio.socket;

import my_netty.nio.channel.AbstractNioChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class encapsulates the one or multiple NioEventLoop, thread-pool and future to support
 * java asynchronous programming.
 *
 * */
public class NioEventLoopGroup {
    private List<NioEventLoop> eventLoops;
    private final AtomicInteger idx = new AtomicInteger();

    public NioEventLoopGroup(){
        eventLoops = new ArrayList<>();
    }

    public NioEventLoop next(){
        //TODO: this.idx.getAndIncrement() is possbile to integer overflow???
        return this.eventLoops.get(this.idx.getAndIncrement() % this.eventLoops.size());
    }

    public void register(AbstractNioChannel channel){
        next().register(channel);
    }
}
