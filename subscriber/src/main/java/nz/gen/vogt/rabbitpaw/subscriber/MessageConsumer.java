package nz.gen.vogt.rabbitpaw.subscriber;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import nz.gen.vogt.rabbitpaw.core.MessageDeserializer;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by Johannes Vogt on 16/08/15.
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
            consumer.accept(deserializer.deserialize(body));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
