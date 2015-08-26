package jv.rabbitfilter.core;

import java.io.IOException;

/**
 * Created by johannes on 23/08/15.
 */
public interface MessageDeserializer<T> {
    T deserialize(byte[] bytes) throws IOException, ClassNotFoundException;
}