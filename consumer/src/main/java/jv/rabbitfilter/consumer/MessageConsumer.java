package jv.rabbitfilter.consumer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

/**
 * Created by johannes on 16/08/15.
 */
public class MessageConsumer<T> extends DefaultConsumer {

    private final Class<T> messageClass;
    private final Consumer<T> consumer;

    public MessageConsumer(Channel channel, Consumer<T> consumer, Class<T> messageClass) {
        super(channel);
        this.messageClass = messageClass;
        this.consumer = consumer;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        try {
            String message = new String(body, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            T object = mapper.readValue(message, messageClass);
            consumer.accept(object);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
