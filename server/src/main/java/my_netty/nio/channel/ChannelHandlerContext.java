package my_netty.nio.channel;

import java.net.SocketAddress;

import static my_netty.nio.channel.ChannelHandlerMask.*;

/**
 * 表示pipeline上的节点。It wraps around ChannelHandler(including inbound and outbound).
 * 它为ChannelHandler维护了一些常用的数据.
 * Expose some apis including both in-bound and out-bound
 *
 * */
public class ChannelHandlerContext {
    private DefaultChannelPipeline pipeline;
    private ChannelHandler handler;

    ChannelHandlerContext prev;
    ChannelHandlerContext next;

    /**
     * The hook methods mask for in/out bound handler.
     * When a handler is added into this context, {@link #executionMask} value
     * will be calculated base on it is in-bound or out-bound, and what hook
     * methods are overridden.
     *
     * This will be used to find the next interested handlers after the current node
     * on the pipeline.
     * */
    private int executionMask;
    public ChannelHandler handler(){
        return handler;
    }

    public ChannelHandlerContext(DefaultChannelPipeline pipeline, ChannelHandler handler){
        this.pipeline = pipeline;
        this.handler = handler;
        this.executionMask = calculateMask(handler);
    }

    private int calculateMask(ChannelHandler handler){
        return ChannelHandlerMask.mask(handler.getClass());
    }

    /**
     * Invoke channelActive() hook method of the next node after current node
     * who overrides the channelActive() hook method.
     * */
    public ChannelHandlerContext fireChannelActive(){
        invokeChannelActive(findContextInbound(MASK_CHANNEL_ACTIVE));
        return this;
    }

    public ChannelHandlerContext fireChannelRead(Object msg){
        invokeChannelRead(findContextInbound(MASK_CHANNEL_READ), msg);
        return this;
    }

    public ChannelHandlerContext fireChannelReadComplete(){
        invokeChannelReadComplete(findContextInbound(MASK_CHANNEL_READ_COMPLETE));
        return this;
    }

    public ChannelHandlerContext fireChannelRegistered(){
        invokeChannelRegistered(findContextInbound(MASK_CHANNEL_REGISTERED));
        return this;
    }

    public ChannelHandlerContext fireChannelUnregistered() {
        invokeChannelUnregistered(findContextInbound(MASK_CHANNEL_REGISTERED));
        return this;
    }

    public ChannelHandlerContext fireChannelInactive() {
        invokeChannelInactive(findContextInbound(MASK_CHANNEL_INACTIVE));
        return this;
    }

    /**
    * Call invokeChannelActive() method starting from {next} in the pipeline.
    *
    * */
    static void invokeChannelActive(final ChannelHandlerContext next) {
        next.invokeChannelActive();
    }

    private void invokeChannelActive(){
        try {
            ((ChannelInboundHandlerAdapter)this.handler()).channelActive(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    static void invokeChannelRead(final ChannelHandlerContext next, Object msg) {
        next.invokeChannelRead(msg);
    }

    private void invokeChannelRead(Object msg){
        try {
            // delegate the channelRead method to the handler wrapped by current context
            ((ChannelInboundHandlerAdapter)this.handler()).channelRead(this, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    static void invokeChannelReadComplete(ChannelHandlerContext next) {
        next.invokeChannelReadComplete();
    }

    private void invokeChannelReadComplete(){
        try {
            ((ChannelInboundHandlerAdapter)this.handler()).channelReadComplete(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    static void invokeChannelInactive(ChannelHandlerContext next) {
        next.invokeChannelInactive();
    }

    private void invokeChannelInactive(){
        try {
            ((ChannelInboundHandlerAdapter)this.handler()).channelInactive(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    static void invokeChannelRegistered(ChannelHandlerContext next) {
        next.invokeChannelRegistered();
    }

    private void invokeChannelRegistered(){
        try {
            ((ChannelInboundHandlerAdapter)this.handler).channelRegistered(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void invokeChannelUnregistered(ChannelHandlerContext next){
        next.invokeChannelUnregistered();
    }

    private void invokeChannelUnregistered(){
        try {
            ((ChannelInboundHandlerAdapter)this.handler()).channelUnregistered(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Search for the next {@link ChannelHandlerContext} node with the specific mask
     * from current.next to tail.
     * */
    private ChannelHandlerContext findContextInbound(int mask) {
        ChannelHandlerContext ctx = this;
        do {
            ctx = ctx.next;
        } while(skipContext(ctx, mask, MASK_ONLY_INBOUND));

        return ctx;
    }

    /**
     * Search for the prev {@link ChannelHandlerContext} node with the specific mask
     * from the current.prev to head.
     * */
    private ChannelHandlerContext findContextOutbound(int mask) {
        ChannelHandlerContext ctx = this;
        do {
            ctx = ctx.prev;
        } while (skipContext(ctx, mask, MASK_ONLY_OUTBOUND));
        return ctx;
    }

    private static boolean skipContext(ChannelHandlerContext ctx, int mask, int onlyMask) {
        return (ctx.executionMask & (onlyMask | mask)) == 0 || (ctx.executionMask & mask) == 0;
    }

    /*Out bound events operations*/
    /**
    * 这个方法跟上面inbound里面的fireXXX() 方法类似，它从current node开始向前
    * 搜索上一个包含MASK_BIND的节点, 然后执行bind() hook method.
    *
    * */
    public void bind(SocketAddress localAddress) {
        final ChannelHandlerContext next = findContextOutbound(MASK_BIND);
        next.invokeBind(localAddress);
    }

    private void invokeBind(SocketAddress localAddress) {
        try {
            ((ChannelOutboundHandlerAdapter) this.handler()).bind(this, localAddress);
        } catch (Throwable t) {
//            notifyOutboundHandlerException(t, promise);
        }
    }

    public void connect(SocketAddress remoteAddress) {
    }

    public void disconnect() {
    }

    public void close() {
    }

    public void flush() {
    }

    public void read() {
    }

    public void writeAndFlush(Object msg) {
        final ChannelHandlerContext next = findContextOutbound(MASK_WRITE | MASK_FLUSH);
        next.invokeWriteAndFlush(msg);
    }

    void invokeWriteAndFlush(Object msg) {
        invokeWriteAndFlush0(msg);
    }

    private void invokeWriteAndFlush0(Object msg) {
        try {
            ((ChannelOutboundHandlerAdapter) handler()).writeAndFlush(this, msg);
        } catch (Throwable t) {
//            notifyOutboundHandlerException(t, promise);
        }
    }

    public void fireExceptionCaught(Throwable cause) {

    }
}
