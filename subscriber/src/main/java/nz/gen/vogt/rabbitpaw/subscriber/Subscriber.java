package nz.gen.vogt.rabbitpaw.subscriber;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import nz.gen.vogt.rabbitpaw.core.MessageDeserializer;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by johannes on 15/08/15.
 */
public class Subscriber<T> {

    private final Binding binding;
    private final Consumer<T> consumer;
    private final MessageDeserializer<T> deserializer;

    private String consumerTag;
    private Channel channel;

    private Subscriber(MessageFilter<T> messageFilter, Consumer<T> consumer, MessageDeserializer<T> deserializer) {
        this.binding = Binding.of(messageFilter);
        this.consumer = consumer;
        if (deserializer == null) {
            this.deserializer = new DefaultDeserializer<>(messageFilter.getMessageConfig().getMessageClass());
        } else {
            this.deserializer = deserializer;
        }
    }

    public void bind(Connection connection) throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        channel = connection.createChannel();

        for (Binding.Stage stage : binding) {
            channel.exchangeDeclare(stage.src, "topic", false, true, null);
            if (stage.isQueueBinding) {
                channel.queueDeclare(stage.dest, false, true, true, null);
                for (String key : stage.keys) {
                    channel.queueBind(stage.dest, stage.src, key);
                    consumerTag = channel.basicConsume(stage.dest, true, new MessageConsumer<>(channel, consumer, deserializer));
                }
            } else {
                channel.exchangeDeclare(stage.dest, "topic", false, true, null);
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

    public static class Builder<T> {
        private MessageFilter<T> messageFilter;
        private Consumer<T> consumer;
        private MessageDeserializer<T> deserializer;

        private Builder() {}

        public Subscriber<T> build() {
            return new Subscriber<>(messageFilter, consumer, deserializer);
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

    }
}
