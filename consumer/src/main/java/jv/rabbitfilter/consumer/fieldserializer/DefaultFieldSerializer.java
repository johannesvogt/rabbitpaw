package jv.rabbitfilter.consumer.fieldserializer;

/**
 * Created by johannes on 23/08/15.
 */
public class DefaultFieldSerializer implements FieldSerializer<Object> {
    @Override
    public String serialize(Object o) {
        return o.toString();
    }
}
