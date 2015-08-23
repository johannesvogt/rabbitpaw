package jv.rabbitfilter.consumer;

import jv.rabbitfilter.core.MessageDeserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by johannes on 23/08/15.
 */
public class DefaultDeserializer<T> implements MessageDeserializer<T> {

    private final Class<T> messageClass;

    public DefaultDeserializer(Class<T> messageClass) {
        this.messageClass = messageClass;
    }

    @Override
    public T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        return messageClass.cast(new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject());
    }
}
