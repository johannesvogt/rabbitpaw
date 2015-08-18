package jv.rabbitfilter.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.function.Consumer;

/**
 * Created by johannes on 15/08/15.
 */
public class FilteredListener<T> {

    private final Class messageClass;

    public FilteredListener(MessageFilter messageFilter, Connection connection, Consumer<T> consumer, Class messageClass) throws IOException {
        this.messageClass = messageClass;

        Channel channel = connection.createChannel();
        createBindings(channel, Routing.create(messageFilter),
                messageClass, new MessageConsumer<T>(channel, consumer, messageClass));
    }

    private void createBindings(Channel channel, Routing routing, Class messageClass, com.rabbitmq.client.Consumer consumer) throws IOException {

        String rootExchange = messageClass.getName();
        channel.exchangeDeclare(rootExchange, "topic", false);


        String queueName = randomId();
        channel.queueDeclare(queueName, false, true, false, null);

        String prevExchange = rootExchange;
        for (Routing.Binding binding : routing) {
            if (binding.isLastLevel) {
                for (String key : binding.keys) {
                    channel.queueBind(queueName, prevExchange, key);
                }
            } else {
                String nextExchange = queueName + binding.level;
                channel.exchangeDeclare(nextExchange, "topic", false);
                for (String key : binding.keys) {
                    channel.exchangeBind(nextExchange, prevExchange, key);
                }
                prevExchange = nextExchange;
            }
        }
    }

    private SecureRandom random = new SecureRandom();

    public String randomId() {
        return new BigInteger(66, random).toString(32);
    }
}
