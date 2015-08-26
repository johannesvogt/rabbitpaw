package nz.gen.vogt.rabbitpaw.demo;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nz.gen.vogt.rabbitpaw.subscriber.Subscriber;
import nz.gen.vogt.rabbitpaw.subscriber.MessageFilter;
import nz.gen.vogt.rabbitpaw.publisher.Publisher;

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


        Subscriber<Article> subscriber = Subscriber.<Article>builder()
                .messageFilter(messageFilter)
                .consumer(System.out::println)
                .build();

        subscriber.bind(connection);

        Publisher<Article> publisher = Publisher.builder(Article.class)
                .addTypeAdapter(Date.class, d -> new SimpleDateFormat("yyyy-MM").format(d))
                .build();

        publisher.bind(connection);

        publisher.publish(new Article("nyt", "food", "abc", new Date()));
        publisher.publish(new Article("drudge", "food", "abc", new Date()));

        subscriber.unbind();
        publisher.unbind();
    }
}
