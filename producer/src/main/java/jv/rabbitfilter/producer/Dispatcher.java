package jv.rabbitfilter.producer;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import jv.rabbitfilter.core.MessageConfig;
import jv.rabbitfilter.core.MessageSerializer;
import jv.rabbitfilter.core.annotation.Filterable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.TreeMap;

/**
 * Created by johannes on 15/08/15.
 */
public class Dispatcher<T> {

    private final Connection connection;

    private final MessageConfig<T> messageConfig;

    private final MessageSerializer<T> serializer;

    private Channel channel;

    public Dispatcher(Connection connection, Class<T> messageClass) {
        this(connection, messageClass, null);
    }

    public Dispatcher(Connection connection, Class<T> messageClass, MessageSerializer<T> serializer) {
        this.connection = connection;
        this.messageConfig = MessageConfig.of(messageClass);
        if (serializer == null) {
            this.serializer = new DefaultSerializer<>();
        } else {
            this.serializer = serializer;
        }
    }

    private void bind() throws IOException {
        channel = connection.createChannel();
        channel.exchangeDeclare(messageConfig.getExchangeName(), "topic", false, true, null);
    }

    public void publish(T message) throws IOException, IllegalAccessException {
        if (channel == null) {
            bind();
        }

        channel.basicPublish(messageConfig.getExchangeName(), getRoutingKey(message), null, serialize(message));
    }

    private String getRoutingKey(T message) throws IllegalAccessException {
        TreeMap<String, String> keyElements = Maps.newTreeMap();
        for (Field field : message.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Filterable.class)) {
                keyElements.put(field.getName(), field.get(message).toString());
            }
        }
        return Joiner.on(".").join(keyElements.values());
    }

    private byte[] serialize(T message) throws IOException {
        return serializer.serialize(message);
    }

}
