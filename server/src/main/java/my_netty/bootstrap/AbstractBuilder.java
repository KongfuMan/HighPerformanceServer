package my_netty.bootstrap;


import my_netty.nio.channel.AbstractNioChannel;
import my_netty.nio.channel.ChannelHandler;
import my_netty.nio.socket.NioEventLoop;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 这是一个server/client builder, 是一个脚手架类(引导类)，
 * 方便用户创建并启动server/client.
 * 采用了builder pattern.
 * */
public abstract class AbstractBuilder {
    private NioEventLoop boss; //single bosss
    protected AbstractNioChannel channel;
    protected ChannelHandler handler;

    public AbstractBuilder(){

    }

    public AbstractBuilder handler(ChannelHandler handler){
        this.handler = handler;
        return this;
    }

    abstract protected AbstractNioChannel newChannel() throws IOException;

    /**
     * For server socket channel: add handler for the channel pipeline;
     *     client socket channel:
     * */
    abstract protected void init(AbstractNioChannel channel);

    AbstractBuilder group(){
        return this;
    }

    /**
     * Socket bind the specific address.
     * Used build TCP/UDP based server, or build UDP based client.
     * To build TCP based client, call connect() method directly.
     *
     * After this method is called,
     * <ul>
     *     <li>A {@link AbstractNioChannel} is created and bind to a port</li>
     *     <li>Register the channel into selector without any interest OP</li>
     * </ul>
     *
     * */
    public void bind(int port){
        this.initAndRegister();
        channel.bind(new InetSocketAddress(port));
    }

    /*
    * Create a channel and register into selector
    * */
    protected void initAndRegister() {
        try {
            AbstractNioChannel channel = this.newChannel();
            /**
             * 这里ops = 0 表示将socket注册到selector中，并不对任何事件感兴趣,
             *因为这里只是初始化，还未执行bind() or connect(), 此时注册任何事件都没有意义
             */
            boss.register(channel, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
