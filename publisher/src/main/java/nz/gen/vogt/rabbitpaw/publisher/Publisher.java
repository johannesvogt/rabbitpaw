package nz.gen.vogt.rabbitpaw.publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import nz.gen.vogt.rabbitpaw.core.MessageConfig;
import nz.gen.vogt.rabbitpaw.core.MessageSerializer;
import nz.gen.vogt.rabbitpaw.core.annotation.RoutingField;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by johannes on 15/08/15.
 */
public class Publisher<T> implements Closeable {

    private final MessageConfig<T> messageConfig;

    private final MessageSerializer<T> serializer;

    private final Map<Class, Function> adapter;

    private Channel channel;

    private Publisher(Class<T> messageClass, MessageSerializer<T> serializer, Map<Class, Function> adapter) {
        this.messageConfig = MessageConfig.of(messageClass);
        if (serializer == null) {
            this.serializer = new DefaultSerializer<>();
        } else {
            this.serializer = serializer;
        }
        this.adapter = adapter;
    }

    public void bind(Connection connection) throws IOException {
        unbind();
        channel = connection.createChannel();
        channel.exchangeDeclare(messageConfig.getExchangeName(), "topic", false, true, null);
    }

    public void unbind() throws IOException {
        close();
    }

    public void publish(T message) throws IOException, IllegalAccessException {
        if (channel == null) {
            throw new IOException("No channel created.");
        }

        channel.basicPublish(messageConfig.getExchangeName(), getRoutingKey(message), null, serialize(message));
    }

    private String getRoutingKey(T message) throws IllegalAccessException {
        TreeMap<String, String> keyElements = new TreeMap<>();
        for (Field field : message.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(RoutingField.class)) {
                keyElements.put(field.getName(), getKeyPart(field.getType(), field.get(message)));
            }
        }
        return keyElements.values().stream().collect(Collectors.joining("."));
    }

    @SuppressWarnings("unchecked")
    private  String getKeyPart(Class fieldClass, Object fieldValue) throws IllegalAccessException {
        if (adapter.containsKey(fieldClass)) {
            return (String)adapter.get(fieldClass).apply(fieldValue);
        } else {
            return fieldValue.toString();
        }
    }

    private byte[] serialize(T message) throws IOException {
        return serializer.serialize(message);
    }

    public static <T> Builder<T> builder(Class<T> messageClass) {
        return new Builder<>(messageClass);
    }

    @Override
    public void close() throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        channel = null;
    }

    public static class Builder<T> {
        private Class<T> messageClass;

        private MessageSerializer<T> serializer;

        private Map<Class, Function> adapter = new HashMap<>();

        private Builder(Class<T> messageClass) {
            this.messageClass = messageClass;
        }

        public Builder<T> serializer(MessageSerializer<T> serializer) {
            this.serializer = serializer;
            return this;
        }

        public <E> Builder<T> addTypeAdapter(Class<E> clazz, Function<E, String> adapterFunction) {
            this.adapter.put(clazz, adapterFunction);
            return this;
        }

        public Publisher<T> build() {
            return new Publisher<>(messageClass, serializer, adapter);
        }
    }

}
