package my_netty.nio.channel;

import my_netty.nio.socket.NioEventLoop;
import org.apache.log4j.Logger;

import java.net.SocketAddress;
import java.nio.channels.*;

/**
 * Wrapper around the java nio selectable socket channel
 * */
public abstract class AbstractNioChannel {
    /**
     * <ul>
     *     <li>
     *         For server channel: {@link #parent} is null;
     *         {@link #channel} is {@link ServerSocketChannel}
     *     </li>
     *     <li>
     *         For client channel: {@link #parent} is the netty nio server;
     *         {@link #channel} is client {@link SocketChannel}
     *     </li>
     * </ul>
     */
    protected AbstractNioChannel parent;
    protected SelectableChannel channel;
    protected final int readInterestOp;
    private DefaultChannelPipeline pipeline;

    private SelectionKey selectionKey;
    private NioEventLoop eventLoop;
    private boolean isAutoRead;
    protected static final Logger logger = Logger.getLogger(AbstractNioChannel.class);

    public AbstractNioChannel(int ops){
        this(null, ops);
    }

    public AbstractNioChannel(AbstractNioChannel parent, int ops){
        this.readInterestOp = ops;
        this.parent = parent;
        this.pipeline = new DefaultChannelPipeline(this);

    }

    public SelectableChannel javaSocketChannel(){
        return channel;
    }

    protected NioEventLoop eventLoop(){
        return eventLoop;
    }

    public DefaultChannelPipeline pipeline(){
        return pipeline;
    }

    public boolean isAutoRead() {
        return isAutoRead;
    }

    protected SelectionKey selectionKey() {
        return selectionKey;
    }

    /**
     * Synchronously register the channel into selector
     * */
    public void register(int ops){
        try {
            // TODO: modify sync method call as asynchronous
            this.selectionKey = javaSocketChannel().register(eventLoop().selector(), ops, this);
            pipeline().fireChannelRegistered();
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    /**
     * bind this channel to a local address
     * */
    public void bind(SocketAddress localAddress){
        // Fire the bind event to the pipeline, the handler
        // itself will take care of do the real binding.
        this.pipeline().bind(localAddress);
        this.pipeline().fireChannelActive();
    }

    public abstract void finishConnect();

    public abstract void forceFlush();

    public abstract void read();

    public abstract void close();

    public abstract void connect(SocketAddress remoteAddress, SocketAddress localAddress);

    public abstract void disconnect();

    public abstract void deregister();

    public abstract void beginRead();

    /**
     * This method is called to write data into the Bytebuffer(memory).
     * It won't flush the buffer data into socket
     * */
    public abstract void write();

    /**
     * This method will call SocketChannel.write(), which will
     * do the actual data sending to client.
     * */
    public abstract void flush();

    public abstract boolean isOpen();


    public abstract void closeForcibly();
}
