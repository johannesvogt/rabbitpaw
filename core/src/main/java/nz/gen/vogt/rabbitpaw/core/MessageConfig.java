package nz.gen.vogt.rabbitpaw.core;

import nz.gen.vogt.rabbitpaw.core.annotation.Filterable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by johannes on 18/08/15.
 */
public class MessageConfig<T> implements Iterable<MessageConfig.FieldEntry> {

    private final Map<String, Class<?>> fields;

    private final Class<T> messageClass;

    private MessageConfig(Class<T> messageClass) {
        Map<String, Class<?>> fieldMap = new TreeMap<String, Class<?>>();
        for (Field field : messageClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Filterable.class)) {
                fieldMap.put(field.getName(), field.getType());
            }
        }
        this.fields = Collections.unmodifiableMap(fieldMap);
        this.messageClass = messageClass;
    }

    public static <T> MessageConfig<T> of(Class<T> messageClass) {
        return new MessageConfig<>(messageClass);
    }

    public Iterator<FieldEntry> iterator() {
        final Iterator<String> iterator = fields.keySet().iterator();
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
        return fields.containsKey(fieldName);
    }

    public Class<T> getMessageClass() {
        return messageClass;
    }

    public Class<?> getFieldType(String fieldName) {
        return fields.get(fieldName);
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
