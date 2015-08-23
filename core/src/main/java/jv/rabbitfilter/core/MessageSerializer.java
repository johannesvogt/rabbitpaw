package jv.rabbitfilter.core;

import java.io.IOException;

/**
 * Created by johannes on 23/08/15.
 */
public interface MessageSerializer<T> {
    byte[] serialize(T t) throws IOException;
}
