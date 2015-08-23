package jv.rabbitfilter.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import jv.rabbitfilter.core.MessageConfig;
import jv.rabbitfilter.core.MessageDeserializer;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by johannes on 16/08/15.
 */
public class MessageConsumer<T> extends DefaultConsumer {

    private final Consumer<T> consumer;
    private final MessageDeserializer<T> deserializer;

    public MessageConsumer(Channel channel, Consumer<T> consumer, MessageDeserializer<T> deserializer) {
        super(channel);
        this.consumer = consumer;
        this.deserializer = deserializer;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        try {
            T message = deserializer.deserialize(body);
            consumer.accept(message);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
