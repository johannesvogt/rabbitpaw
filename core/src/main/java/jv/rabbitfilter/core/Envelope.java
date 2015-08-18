package jv.rabbitfilter.core;

/**
 * Created by johannes on 15/08/15.
 */
public class Envelope {

    private final String routingKey;

    private final byte[] bytes;

    public Envelope(String routingKey, byte[] bytes) {
        this.routingKey = routingKey;
        this.bytes = bytes;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
