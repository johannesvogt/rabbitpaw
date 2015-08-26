package nz.gen.vogt.rabbitpaw.subscriber;

import nz.gen.vogt.rabbitpaw.core.MessageConfig;

import java.util.*;

/**
 * Created by johannes on 15/08/15.
 */
public class MessageFilter<T> {

    private final MessageConfig<T> messageConfig;

    private final Map<String, List<String>> filterParams;

    private MessageFilter(Class<T> messageClass) {
        this.filterParams = new HashMap<>();
        this.messageConfig = MessageConfig.of(messageClass);
    }

    public static <T> MessageFilter<T> of(Class<T> messageClass) {
        return new MessageFilter<>(messageClass);
    }

    public MessageFilter<T> setParameter(String fieldName, String... fieldValues) {
        if (!messageConfig.contains(fieldName)) {
            throw new IllegalArgumentException("Field '" + fieldName + "' not known for type '" + messageConfig.getMessageClass() + "'.");
        }
        filterParams.put(fieldName, Collections.unmodifiableList(Arrays.asList(fieldValues)));
        return this;
    }

    List<String> getParam(String field) {
        return filterParams.get(field);
    }

    public MessageConfig<T> getMessageConfig() {
        return messageConfig;
    }

}
