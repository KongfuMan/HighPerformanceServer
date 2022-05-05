package my_netty.nio.channel;

import my_netty.nio.channel.ChannelHandlerMask.Skip;

/**
 * Lifecycle hooks for handling in-bound events.
 * It use adapter design pattern to expose a unified api to user.
 *
 * User can extends this class to override the hook method.
 * If a subclass don't override the hook method, that means the subclass
 * is not interested in such hooks.
 *
 * */
public class ChannelInboundHandlerAdapter extends ChannelHandler {
    public ChannelInboundHandlerAdapter() {
    }

    @Skip
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    @Skip
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelUnregistered();
    }

    @Skip
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    @Skip
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }

    @Skip
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
    }

    @Skip
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

//    @Skip
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        ctx.fireUserEventTriggered(evt);
//    }
//
//    @Skip
//    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
//        ctx.fireChannelWritabilityChanged();
//    }
//
//    @Skip
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        ctx.fireExceptionCaught(cause);
//    }
}
