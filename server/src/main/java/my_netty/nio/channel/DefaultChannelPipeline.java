package my_netty.nio.channel;

import java.net.SocketAddress;

/**
 * Represents a pipeline associated with a specific channel(either server or client).
 * The pipeline contains head and tail of a doubly linklist.
 * Linklist node is type of {@link ChannelHandlerContext}, which wraps around {@link ChannelInboundHandlerAdapter} and
 * {@link ChannelOutboundHandlerAdapter}
 *
 * Pipeline does NOT differentiate the in-bound or out-bound event.
 */
public class DefaultChannelPipeline {
    ChannelHandlerContext head;
    ChannelHandlerContext tail;

    private AbstractNioChannel channel;

    public DefaultChannelPipeline(AbstractNioChannel channel) {
        this.channel = channel;
//        tail = new TailContext(this);
//        head = new HeadContext(this);

        head.next = tail;
        tail.prev = head;
    }

    public void addLast(ChannelHandler handler){
        ChannelHandlerContext ctx = new ChannelHandlerContext(this, handler);
        addLast0(ctx);
    }

    public AbstractNioChannel channel(){
        return channel;
    }

    private void addLast0(ChannelHandlerContext newCtx) {
        ChannelHandlerContext prev = this.tail.prev;
        newCtx.prev = prev;
        newCtx.next = this.tail;
        prev.next = newCtx;
        this.tail.prev = newCtx;
    }

    private void addFirst(ChannelHandlerContext newCtx) {
        ChannelHandlerContext nextCtx = this.head.next;
        newCtx.prev = this.head;
        newCtx.next = nextCtx;
        this.head.next = newCtx;
        nextCtx.prev = newCtx;
    }

    /****This is the in-bound data flow from head node to tail node ****/
    public final DefaultChannelPipeline fireChannelActive() {
        /*
          Invoke channelActive() method, starting from head, for each node of the pipeline who
          override the channelActive() method.
         */
        ChannelHandlerContext.invokeChannelActive(head);
        return this;
    }

    public final DefaultChannelPipeline fireChannelInactive() {
        ChannelHandlerContext.invokeChannelInactive(head);
        return this;
    }

    public final DefaultChannelPipeline fireChannelRead(Object msg) {
        ChannelHandlerContext.invokeChannelRead(head, msg);
        return this;
    }

    public final DefaultChannelPipeline fireChannelReadComplete() {
        ChannelHandlerContext.invokeChannelReadComplete(head);
        return this;
    }

    public final DefaultChannelPipeline fireChannelRegistered() {
        ChannelHandlerContext.invokeChannelRegistered(head);
        return this;
    }

    public final DefaultChannelPipeline fireChannelClosed(){
        return this;
    }

    public final DefaultChannelPipeline fireExceptionCaught(Throwable t) {
        return this;
    }


    //TODO: more hook methods

    /****This is the out-bound data flow from tail node to head node ****/
    /*
    * This is similar as the invokeXXX method for inbound hooks.
    * */
    public void bind(SocketAddress localAddress) {
        tail.bind(localAddress);
    }

    public void connect(SocketAddress remoteAddress) {
        tail.connect(remoteAddress);
    }

    public void disconnect() {
        tail.disconnect();
    }

    public void close() {
        tail.close();
    }

    public final DefaultChannelPipeline flush() {
        tail.flush();
        return this;
    }

    public final DefaultChannelPipeline read() {
        tail.read();
        return this;
    }

    public void writeAndFlush(Object msg) {
        tail.writeAndFlush(msg);
    }


    public void fireChannelWrite() {
    }
}


