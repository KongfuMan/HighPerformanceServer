package my_netty.bootstrap;

import my_netty.nio.channel.*;

import java.io.IOException;

/**
 * A {@link ClientBuilder} that makes it easy to bootstrap a {@link Channel} to use
 * for clients.
 *
 * <p>The {@link #bind(int port)} methods are useful in combination with connectionless transports such as datagram (UDP).
 * For regular TCP connections, please use the provided {@link #connect()} methods.</p>
 */
public final class ClientBuilder extends AbstractBuilder {

    @Override
    protected AbstractNioChannel newChannel() throws IOException {
        this.channel = new NioSocketChannel();
        return this.channel;
    }

    @Override
    protected void init(AbstractNioChannel channel) {

    }

    /**
     * Initiate connection to server side.
     * To improve the efficiency, only after this method is called:
     *   - (Client)SocketChannel is opened(created).
     *   -
     * */
    public void connect(){

    }
}
