package my_netty.handlers;

import my_netty.nio.channel.AbstractNioChannel;
import my_netty.nio.channel.ChannelHandler;
import my_netty.nio.channel.ChannelHandlerContext;
import my_netty.nio.channel.ChannelInboundHandlerAdapter;
import my_netty.nio.socket.NioEventLoopGroup;

import java.util.concurrent.TimeUnit;

public class ServerBootstrapAcceptor extends ChannelInboundHandlerAdapter {
    private final NioEventLoopGroup workerGroup;
    private final ChannelHandler childHandler;

    ServerBootstrapAcceptor(final AbstractNioChannel channel, NioEventLoopGroup childGroup, ChannelHandler childHandler) {
        this.workerGroup = childGroup;
        this.childHandler = childHandler;
    }

    /**
     * 在这个方法中来把新创建的Accepted socket channel 给注册到workerGroup中
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        final AbstractNioChannel child = (AbstractNioChannel) msg;
        child.pipeline().addLast(this.childHandler);
        try {
            this.workerGroup.register(child);
        } catch (Throwable var5) {
            forceClose(child, var5);
        }

    }

    private static void forceClose(AbstractNioChannel child, Throwable t) {
        child.closeForcibly();
    }
}