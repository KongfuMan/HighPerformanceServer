package my_netty.nio.channel;

import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import my_netty.nio.channel.ChannelHandlerMask.*;

/**
 * Lifecycle hooks for out-bound events
 * 注意：After override handler中的lifecycle hook methods, 如果你想让前面的
 * handler继续执行相同的method的话，需要手动call super.XXX(...). 如果不手动call的话，
 * 会让后面的handler的钩子方法无法被执行。
 * */
public abstract class ChannelOutboundHandlerAdapter extends ChannelHandler {
    public ChannelOutboundHandlerAdapter() {
    }

    /**
     * Called by server socket channel to bind and listen to a local address.
     * */
    @Skip
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress) throws Exception{
        ctx.bind(localAddress);
    }

    /**
     * Called by client socket channel to make connection request to server.
     * Note: this is the workflow for C/S architecture. For pure socket programming, there is
     * no client and server.
     * */
    @Skip
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress) throws Exception{
        ctx.connect(remoteAddress);
    }

    @Skip
    public void disconnect(ChannelHandlerContext ctx) throws Exception{
        ctx.disconnect();
        ServerSocketChannel server;
        SocketChannel client;
    }

    @Skip
    public void close(ChannelHandlerContext ctx) throws Exception{
        ctx.close();
    }

    @Skip
    public void deregister(ChannelHandlerContext ctx) throws Exception{
        //TODO
    }

    @Skip
    public void read(ChannelHandlerContext ctx) throws Exception{
        ctx.read();
    }

    @Skip
    public void writeAndFlush(ChannelHandlerContext ctx, Object msg) throws Exception{
        ctx.writeAndFlush(msg);
    }
}
