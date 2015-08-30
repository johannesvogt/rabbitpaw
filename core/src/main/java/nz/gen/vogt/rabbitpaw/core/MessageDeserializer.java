package nz.gen.vogt.rabbitpaw.core;

import java.io.IOException;

/**
 * Created by Johannes Vogt on 23/08/15.
 */
public interface MessageDeserializer<T> {
    T deserialize(byte[] bytes) throws IOException, ClassNotFoundException;
}
