package jv.rabbitfilter.demo;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jv.rabbitfilter.consumer.FilteredListener;
import jv.rabbitfilter.consumer.MessageFilter;
import jv.rabbitfilter.producer.Dispatcher;

import java.io.IOException;

/**
 * Created by johannes on 15/08/15.
 */
public class Main {
    public static void main(String[] args) throws IOException, IllegalAccessException {
//        Article article = new Article("my title", "my abstract", "my author", new Date());
//
//        new Producer().publish(article);
//
//        System.out.println("Done.");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();

        MessageFilter<Article> messageFilter = MessageFilter.of(Article.class)
                .setParameter("author", "abc")
                .setParameter("author", "def")
                .setParameter("publisher", "drudge")
                .setParameter("publisher", "nyt")
                .setParameter("year", "2015")
                .setParameter("category", "cars")
                .setParameter("category", "food")
                .setParameter("category", "animals");


        FilteredListener<Article> filteredListener = FilteredListener.<Article>builder()
                .connection(connection)
                .consumer(System.out::println)
                .messageFilter(messageFilter)
                .build();

        filteredListener.bind();


        Dispatcher<Article> dispatcher = new Dispatcher<Article>(connection, Article.class);

        dispatcher.publish(new Article("nyt", "food", "abc", "2015"));
        dispatcher.publish(new Article("drudge", "food", "abc", "2015"));

//        filteredListener.unbind();
    }
}
