package my_netty.handlers;

import my_netty.nio.channel.*;

import java.net.SocketAddress;

final class HeadContext extends ChannelHandlerContext implements ChannelOutboundHandlerAdapter, ChannelInboundHandlerAdapter {
    private final AbstractNioChannel ch;

    HeadContext(DefaultChannelPipeline pipeline) {
        super(pipeline, HeadContext.class);
        this.ch = pipeline.channel();
    }

    public ChannelHandler handler() {
        return this;
    }

    public void handlerAdded(ChannelHandlerContext ctx) {
    }

    public void handlerRemoved(ChannelHandlerContext ctx) {
    }

    /**
     * This is the hook method that do the actual channel bind
     * */
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress) {
        this.ch.bind(localAddress);
    }

    /**
     * This is the hook method that do the actual connect
     * */
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress) {
        this.ch.connect(remoteAddress, localAddress);
    }

    public void disconnect(ChannelHandlerContext ctx) {
        this.ch.disconnect();
    }

    public void close(ChannelHandlerContext ctx) {
        this.ch.close();
    }

    public void deregister(ChannelHandlerContext ctx) {
        this.ch.deregister();
    }

    /**
     * {@link HeadContext} 作为in-bound把门的节点, read是in-bound event.
     * 那么可以在这个方法中，准备读。
     * */
    public void read(ChannelHandlerContext ctx) {
        this.ch.beginRead();
    }

    public void write(ChannelHandlerContext ctx, Object msg) {
        this.ch.write();
    }

    public void flush(ChannelHandlerContext ctx) {
        this.ch.flush();
    }

    public void channelActive(ChannelHandlerContext ctx) {
        ctx.fireChannelActive();
        this.readIfIsAutoRead();
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        super.fireChannelReadComplete();
        this.readIfIsAutoRead();
    }

    private void readIfIsAutoRead() {
        if (this.ch.isAutoRead()) {
            this.ch.read();
        }

    }
}
