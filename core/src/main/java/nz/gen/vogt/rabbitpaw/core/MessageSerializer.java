package nz.gen.vogt.rabbitpaw.core;

import java.io.IOException;

/**
 * Created by Johannes Vogt on 23/08/15.
 */
public interface MessageSerializer<T> {
    byte[] serialize(T t) throws IOException;
}
