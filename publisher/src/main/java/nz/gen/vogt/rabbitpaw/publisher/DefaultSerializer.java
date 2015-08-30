package nz.gen.vogt.rabbitpaw.publisher;

import nz.gen.vogt.rabbitpaw.core.MessageSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by Johannes Vogt on 23/08/15.
 */
public class DefaultSerializer<T> implements MessageSerializer<T> {
    @Override
    public byte[] serialize(T t) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        new ObjectOutputStream(b).writeObject(t);
        return b.toByteArray();
    }
}
