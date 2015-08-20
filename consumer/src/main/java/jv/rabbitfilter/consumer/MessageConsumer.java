package jv.rabbitfilter.consumer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import jv.rabbitfilter.core.MessageConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

/**
 * Created by johannes on 16/08/15.
 */
public class MessageConsumer<T> extends DefaultConsumer implements ShutdownListener {

    private final MessageConfig<T> messageConfig;
    private final Consumer<T> consumer;

    public MessageConsumer(Channel channel, Consumer<T> consumer, MessageConfig<T> messageConfig) {
        super(channel);
        this.messageConfig = messageConfig;
        this.consumer = consumer;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        try {
            T message = messageConfig.getMessageClass()
                    .cast(new ObjectInputStream(new ByteArrayInputStream(body)).readObject());
            consumer.accept(message);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownCompleted(ShutdownSignalException cause) {

    }
}
