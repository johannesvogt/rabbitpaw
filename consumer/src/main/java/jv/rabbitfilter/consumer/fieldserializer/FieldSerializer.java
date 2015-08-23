package jv.rabbitfilter.consumer.fieldserializer;

/**
 * Created by johannes on 23/08/15.
 */
public interface FieldSerializer<T> {
    String serialize(T t);
}
