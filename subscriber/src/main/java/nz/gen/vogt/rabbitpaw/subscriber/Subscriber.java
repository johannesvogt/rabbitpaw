package nz.gen.vogt.rabbitpaw.subscriber;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import nz.gen.vogt.rabbitpaw.core.MessageDeserializer;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by Johannes Vogt on 15/08/15.
 */
public class Subscriber<T> implements Closeable {

    private final Binding binding;
    private final Consumer<T> consumer;
    private final MessageDeserializer<T> deserializer;
    private final QueueConfig queueConfig;

    private String consumerTag;
    private Channel channel;

    private Subscriber(MessageFilter<T> messageFilter, Consumer<T> consumer,
                       MessageDeserializer<T> deserializer, QueueConfig queueConfig) {
        this.binding = Binding.of(messageFilter);
        this.consumer = consumer;
        if (deserializer == null) {
            this.deserializer = new DefaultDeserializer<>(messageFilter.getMessageConfig().getMessageClass());
        } else {
            this.deserializer = deserializer;
        }
        if (queueConfig == null) {
            this.queueConfig = new QueueConfig(false, true, null);
        } else {
            this.queueConfig = queueConfig;
        }
    }

    public void bind(Connection connection) throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        channel = connection.createChannel();

        for (Binding.Stage stage : binding) {
            channel.exchangeDeclare(stage.src, "topic", queueConfig.isDurable(), queueConfig.isAutoDelete(), queueConfig.getArguments());
            if (stage.isQueueBinding) {
                channel.queueDeclare(stage.dest, queueConfig.isDurable(), true, queueConfig.isAutoDelete(), queueConfig.getArguments());
                for (String key : stage.keys) {
                    channel.queueBind(stage.dest, stage.src, key);
                    consumerTag = channel.basicConsume(stage.dest, true, new MessageConsumer<>(channel, consumer, deserializer));
                }
            } else {
                channel.exchangeDeclare(stage.dest, "topic", queueConfig.isDurable(), queueConfig.isAutoDelete(), queueConfig.getArguments());
                for (String key : stage.keys) {
                    channel.exchangeBind(stage.dest, stage.src, key);
                }
            }
        }
    }

    public void unbind() throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.basicCancel(consumerTag);
            for (Binding.Stage stage : binding) {
                if (stage.isQueueBinding) {
                    for (String key : stage.keys) {
                        channel.queueUnbind(stage.dest, stage.src, key);
                    }
                } else {
                    for (String key : stage.keys) {
                        channel.exchangeUnbind(stage.dest, stage.src, key);
                    }
                }
            }
            channel.close();
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    @Override
    public void close() throws IOException {
        unbind();
        channel = null;
    }

    public static class Builder<T> {
        private MessageFilter<T> messageFilter;
        private Consumer<T> consumer;
        private MessageDeserializer<T> deserializer;
        private QueueConfig queueConfig;

        private Builder() {}

        public Subscriber<T> build() {
            return new Subscriber<>(messageFilter, consumer, deserializer, queueConfig);
        }

        public Builder<T> messageFilter(MessageFilter<T> messageFilter) {
            this.messageFilter = messageFilter;
            return this;
        }

        public Builder<T> consumer(Consumer<T> consumer) {
            this.consumer = consumer;
            return this;
        }

        public Builder<T> messageDeserializer(MessageDeserializer<T> deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public Builder<T> queueConfig(QueueConfig queueConfig) {
            this.queueConfig = queueConfig;
            return this;
        }

    }
}
