package my_netty.nio.channel;

/**
 * A wrapper around the {@link java.nio.ByteBuffer}
 * Some enhancement:
 * 1. Resolve the TCP packet stick and unpack issue
 * 2.
 *
 * One socket channel should have an independent {@link ByteBuf},
 * which is saved as an attachment of the channel.
 */
public class ByteBuf {

}
