package NIO.SingleThreadNIOV2.common;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * A channel encapsulates the java nio SocketChannel and associated (send/receive)buffer
 *
 * */
public class HPChannel {
    // id of node this channel connects with.
    private String id;
    private HPSend send;
    private HPReceive receive;
    private TransportLayer transportLayer;
//    private SocketAddress remoteAddress;

    public HPChannel(){
    }

    public boolean finishConnect() throws IOException {
        return this.transportLayer.finishConnect();
    }

    public String id() {
        return this.id;
    }
}
