package jv.rabbitfilter.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import jv.rabbitfilter.core.annotation.Filterable;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

/**
 * Created by johannes on 18/08/15.
 */
public class MessageConfig<T> implements Iterable<MessageConfig.FieldEntry> {

    private final List<String> fields;

    private final Class<T> messageClass;

    private MessageConfig(Class<T> messageClass) {
        ImmutableSortedSet.Builder<String> setBuilder = ImmutableSortedSet.naturalOrder();
        for (Field field : messageClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Filterable.class)) {
                setBuilder.add(field.getName());
            }
        }
        this.fields = ImmutableList.copyOf(setBuilder.build());
        this.messageClass = messageClass;
    }

    public static MessageConfig of(Class messageClass) {
        return new MessageConfig(messageClass);
    }

    public Iterator<FieldEntry> iterator() {
        final Iterator<String> iterator = fields.iterator();
        final int[] index = new int[]{0};
        return new Iterator<FieldEntry>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public FieldEntry next() {
                String name = iterator.next();
                return new FieldEntry(name, !iterator.hasNext(), index[0]++);
            }
        };
    }

    public boolean contains(String fieldName) {
        return fields.contains(fieldName);
    }

    public Class<T> getMessageClass() {
        return messageClass;
    }

    public static class FieldEntry {
        public final String name;
        public final int index;
        public final boolean isLast;

        public FieldEntry(String name, boolean isLast, int index) {
            this.name = name;
            this.isLast = isLast;
            this.index = index;
        }

    }

    public int size() {
        return fields.size();
    }

    public String getExchangeName() {
        return messageClass.getName();
    }

}
