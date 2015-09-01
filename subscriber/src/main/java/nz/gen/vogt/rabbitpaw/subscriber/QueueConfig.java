package nz.gen.vogt.rabbitpaw.subscriber;

import java.util.Map;

/**
 * Created by Johannes Vogt on 30/08/15.
 */
public class QueueConfig {

    private final boolean durable;

    private final boolean autoDelete;

    private final Map<String,Object> arguments;

    public QueueConfig(boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        this.durable = durable;
        this.autoDelete = autoDelete;
        this.arguments = arguments;
    }

    public boolean isDurable() {
        return durable;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }
}
