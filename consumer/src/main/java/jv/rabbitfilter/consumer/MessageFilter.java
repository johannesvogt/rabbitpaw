package jv.rabbitfilter.consumer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import jv.rabbitfilter.core.MessageConfig;

import java.util.List;

/**
 * Created by johannes on 15/08/15.
 */
public class MessageFilter<T> {

    private final MessageConfig<T> messageConfig;

    private final Multimap<String,String> filterParams;

    private MessageFilter(Class<T> messageClass) {
        this.filterParams = HashMultimap.create();
        this.messageConfig = MessageConfig.of(messageClass);
    }

    public static <T> MessageFilter<T> of(Class<T> messageClass) {
        return new MessageFilter<>(messageClass);
    }

    public MessageFilter<T> setParameter(String fieldName, String... fieldValues) {
        if (!messageConfig.contains(fieldName)) {
            throw new IllegalArgumentException("Field '" + fieldName + "' not known for type '" + messageConfig.getMessageClass() + "'.");
        }
        for (String fieldValue : fieldValues) {
            filterParams.put(fieldName, fieldValue);
        }
        return this;
    }

    List<String> getParam(String field) {
        return Lists.newArrayList(filterParams.get(field));
    }

    public MessageConfig<T> getMessageConfig() {
        return messageConfig;
    }

}
