package NIO.SingleThreadNIOV2.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_CONNECT;

public class HPSelector {
    private Selector nioSelector;
    private Map<String, HPChannel> channels;
    private Set<String> connected;

    /**
     * initiate a connection to address
     * @param id of the Node to connect to.
      */
    public void connect(String id, InetSocketAddress address)
            throws IOException {
        // throw exception if we failed to open
        SocketChannel channel = SocketChannel.open();
        SelectionKey key = null;
        try {
            configureSocketChannel(channel);
            boolean connected = channel.connect(address);
            if (!connected) {
                key = registerChannel(id, channel, OP_CONNECT);
            } else {

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * register the
     * */
    private SelectionKey registerChannel(String id, SocketChannel channel, int ops)
            throws ClosedChannelException {
        SelectionKey key = channel.register(this.nioSelector, ops);
        HPChannel hpChannel = buildAndAttachChannel(id, key);
        this.channels.put(id, hpChannel);
        return key;
    }

    /**
     * Build HpChannel and attach to the key.
     * */
    private HPChannel buildAndAttachChannel(String id, SelectionKey key) {
        // TODO: build and attach HPChannel: send/receive buffer + transport layer.
        return null;
    }

    private void configureSocketChannel(SocketChannel channel)
            throws IOException {
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        socket.setKeepAlive(true); // long TCP connection
        socket.setTcpNoDelay(true);// send data packet without lingering
    }

    public void poll() {
        try {
            this.nioSelector.selectNow();
            processSelectionKeys();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processSelectionKeys() {
        Set<SelectionKey> selectionKeys = this.nioSelector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while(iterator.hasNext()){
            SelectionKey key = iterator.next();
            iterator.remove();
            processSelectionKey(key);
        }
    }

    private void processSelectionKey(SelectionKey key) {
        HPChannel channel = (HPChannel)key.attachment();
        try {
            if (key.isConnectable()) {
                if (channel.finishConnect()) {
                    this.connected.add(channel.id());
                }
            }

            if (key.isReadable()) {
            }

            if (key.isWritable()) {
            }
        } catch (IOException e) {
        }
    }
}
