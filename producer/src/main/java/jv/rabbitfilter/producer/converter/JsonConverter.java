package jv.rabbitfilter.producer.converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import jv.rabbitfilter.core.Envelope;
import jv.rabbitfilter.core.annotation.Filterable;

import java.lang.reflect.Field;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by johannes on 15/08/15.
 */
public class JsonConverter {

    public Envelope convert(Object object) throws IllegalAccessException, JsonProcessingException {

        return new Envelope(Joiner.on(Character.toString((char)30)).join(getFilterables(object).values()),
                getJson(object).getBytes());

    }

    private SortedMap<String, String> getFilterables(Object object) throws IllegalAccessException {
        TreeMap<String, String> filterables = Maps.newTreeMap();
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Filterable.class)) {
                filterables.put(field.getName(), field.get(object).toString().replaceAll("\u001E", ""));
            }
        }
        return filterables;
    }

    private String getJson(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.writeValueAsString(object);
    }
}
