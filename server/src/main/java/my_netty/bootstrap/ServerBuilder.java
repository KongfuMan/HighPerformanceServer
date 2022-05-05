package my_netty.bootstrap;

import my_netty.nio.channel.AbstractNioChannel;
import my_netty.nio.channel.ChannelHandler;
import my_netty.nio.channel.NioServerSocketChannel;
import my_netty.nio.socket.NioEventLoopGroup;

import java.io.IOException;

public final class ServerBuilder extends AbstractBuilder {
    private NioEventLoopGroup worker; // worker is a group of NioEventLoop
    private ChannelHandler workerHandler;

    public ServerBuilder() {
        super();
    }

    public AbstractBuilder workerHandler(ChannelHandler handlers){
        this.workerHandler = handlers;
        return this;
    }

    @Override
    protected AbstractNioChannel newChannel() throws IOException {
        this.channel = new NioServerSocketChannel();
        return this.channel;
    }

    @Override
    protected void init(AbstractNioChannel channel) {
        //channel is the server socket channel
        channel.pipeline().addLast(this.handler);
    }
}
