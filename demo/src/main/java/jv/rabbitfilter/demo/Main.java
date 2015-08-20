package jv.rabbitfilter.demo;

import com.google.common.collect.ImmutableList;
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
                .thatMatches("author", "abc")
                .thatMatches("author", "def")
                .thatMatches("publisher", "drudge")
                .thatMatches("publisher", "nyt")
                .thatMatches("year", "2015")
                .thatMatches("category", "cars")
                .thatMatches("category", "food")
                .thatMatches("category", "animals");


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
