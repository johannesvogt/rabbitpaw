package jv.rabbitfilter.demo;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jv.rabbitfilter.consumer.FilteredListener;
import jv.rabbitfilter.consumer.MessageFilter;
import jv.rabbitfilter.producer.Dispatcher;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by johannes on 15/08/15.
 */
public class Main {
    public static void main(String[] args) throws IOException, IllegalAccessException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();

        MessageFilter<Article> messageFilter = MessageFilter.of(Article.class)
                .setParameter("author", "abc", "def")
                .setParameter("publisher", "drudge", "nyt")
                .setParameter("year", "2015-08")
                .setParameter("category", "cars", "food", "animals");


        FilteredListener<Article> filteredListener = FilteredListener.<Article>builder()
                .messageFilter(messageFilter)
                .consumer(System.out::println)
                .build();

        filteredListener.bind(connection);

        Dispatcher<Article> dispatcher = Dispatcher.builder(Article.class)
                .addTypeAdapter(Date.class, d -> new SimpleDateFormat("yyyy-MM").format(d))
                .build();

        dispatcher.bind(connection);

        dispatcher.publish(new Article("nyt", "food", "abc", new Date()));
        dispatcher.publish(new Article("drudge", "food", "abc", new Date()));

        filteredListener.unbind();
        dispatcher.unbind();
    }
}
