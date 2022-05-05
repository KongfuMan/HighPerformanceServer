package my_netty.handlers;

import my_netty.nio.channel.ChannelHandler;
import my_netty.nio.channel.ChannelHandlerContext;
import my_netty.nio.channel.DefaultChannelPipeline;

final class TailContext extends ChannelHandlerContext implements ChannelInboundHandler {
    TailContext(DefaultChannelPipeline pipeline) {
        super(pipeline, this);
    }

    public ChannelHandler handler() {
        return this;
    }

    public void channelRegistered(ChannelHandlerContext ctx) {
    }

    public void channelUnregistered(ChannelHandlerContext ctx) {
    }

    public void channelActive(ChannelHandlerContext ctx) {
        DefaultChannelPipeline.this.onUnhandledInboundChannelActive();
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        DefaultChannelPipeline.this.onUnhandledInboundChannelInactive();
    }

    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        DefaultChannelPipeline.this.onUnhandledChannelWritabilityChanged();
    }

    public void handlerAdded(ChannelHandlerContext ctx) {
    }

    public void handlerRemoved(ChannelHandlerContext ctx) {
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        DefaultChannelPipeline.this.onUnhandledInboundUserEventTriggered(evt);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        DefaultChannelPipeline.this.onUnhandledInboundException(cause);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        DefaultChannelPipeline.this.onUnhandledInboundMessage(ctx, msg);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        DefaultChannelPipeline.this.onUnhandledInboundChannelReadComplete();
    }
}