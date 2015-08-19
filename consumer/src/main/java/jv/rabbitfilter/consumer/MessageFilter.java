package jv.rabbitfilter.consumer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import jv.rabbitfilter.core.MessageConfig;

import java.util.List;

/**
 * Created by johannes on 15/08/15.
 */
public class MessageFilter {

    private final Class messageClass;

    private final MessageConfig messageConfig;

    private final Multimap<String,String> filterParams;

    private MessageFilter(Class messageClass) {
        this.messageClass = messageClass;
        this.filterParams = HashMultimap.create();
        this.messageConfig = MessageConfig.of(messageClass);
    }

    public static MessageFilter of(Class messageClass) {
        return new MessageFilter(messageClass);
    }

    public MessageFilter thatMatches(String fieldName, String fieldValue) {
        if (!messageConfig.contains(fieldName)) {
            throw new IllegalArgumentException("Field '" + fieldName + "' not known for type '" + messageClass + "'.");
        }
        filterParams.put(fieldName, fieldValue);
        return this;
    }

    List<String> getParam(String field) {
        return Lists.newArrayList(filterParams.get(field));
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

}
