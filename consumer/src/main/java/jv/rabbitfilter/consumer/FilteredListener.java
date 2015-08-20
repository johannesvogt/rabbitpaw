package jv.rabbitfilter.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import jv.rabbitfilter.core.MessageConfig;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.function.Consumer;

/**
 * Created by johannes on 15/08/15.
 */
public class FilteredListener<T> {

    private final Connection connection;
    private final Binding binding;
    private final Consumer<T> consumer;
    private final MessageConfig<T> messageConfig;

    private String queueName;
    private String consumerTag;
    private Channel channel;

    private FilteredListener(MessageFilter<T> messageFilter, Connection connection, Consumer<T> consumer) {
        this.connection = connection;
        this.binding = Binding.of(messageFilter);
        this.consumer = consumer;
        this.messageConfig = messageFilter.getMessageConfig();

    }

    public void bind() throws IOException {
        channel = connection.createChannel();

        String rootExchange = messageConfig.getExchangeName();
        channel.exchangeDeclare(rootExchange, "topic", false, true, null);


        queueName = randomId();
        channel.queueDeclare(queueName, false, true, true, null);

        String prevExchange = rootExchange;
        for (Binding.Stage stage : binding) {
            if (stage.isLast) {
                for (String key : stage.keys) {
                    channel.queueBind(queueName, prevExchange, key);
                    consumerTag = channel.basicConsume(queueName, true, new MessageConsumer<T>(channel, consumer, messageConfig));
                }
            } else {
                String nextExchange = queueName + stage.level;
                channel.exchangeDeclare(nextExchange, "topic", false, true, null);
                for (String key : stage.keys) {
                    channel.exchangeBind(nextExchange, prevExchange, key);
                }
                prevExchange = nextExchange;
            }
        }
    }

    public void unbind() throws IOException {
        channel.basicCancel(consumerTag);
        String prevExchange = messageConfig.getExchangeName();
        for (Binding.Stage stage : binding) {
            if (stage.isLast) {
                for (String key : stage.keys) {
                    channel.queueUnbind(queueName, prevExchange, key);
                }
            } else {
                String nextExchange = queueName + stage.level;
                for (String key : stage.keys) {
                    channel.exchangeUnbind(nextExchange, prevExchange, key);
                }
                prevExchange = nextExchange;
            }
        }
    }

    private SecureRandom random = new SecureRandom();

    private String randomId() {
        return new BigInteger(130, random).toString(32);
    }

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static class Builder<T> {
        private MessageFilter<T> messageFilter;
        private Consumer<T> consumer;
        private Connection connection;

        private Builder() {}

        public FilteredListener<T> build() {
            return new FilteredListener<T>(messageFilter, connection, consumer);
        }

        public Builder<T> messageFilter(MessageFilter<T> messageFilter) {
            this.messageFilter = messageFilter;
            return this;
        }

        public Builder<T> consumer(Consumer<T> consumer) {
            this.consumer = consumer;
            return this;
        }

        public Builder<T> connection(Connection connection) {
            this.connection = connection;
            return this;
        }
    }
}
