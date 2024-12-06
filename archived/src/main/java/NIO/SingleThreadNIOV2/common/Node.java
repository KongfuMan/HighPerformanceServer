package NIO.SingleThreadNIOV2.common;

/**
 * Represent a remote endpoint.
 * */
public class Node {
    private String id;
    private String ip;
    private int port;

    public String id() {
        return this.id;
    }
}
