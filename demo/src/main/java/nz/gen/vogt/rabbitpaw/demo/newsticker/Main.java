package nz.gen.vogt.rabbitpaw.demo.newsticker;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nz.gen.vogt.rabbitpaw.publisher.Publisher;
import nz.gen.vogt.rabbitpaw.subscriber.MessageFilter;
import nz.gen.vogt.rabbitpaw.subscriber.Subscriber;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static nz.gen.vogt.rabbitpaw.demo.newsticker.NewsItem.Category.*;
import static nz.gen.vogt.rabbitpaw.demo.newsticker.NewsItem.Newspaper.*;

/**
 * Created by Johannes Vogt on 15/08/15.
 */
public class Main {
    public static void main(String[] args) throws IOException, IllegalAccessException, InterruptedException {

        // create connection to rabbitmq - server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();

        // subscriber 1
        MessageFilter<NewsItem> filter1 = MessageFilter.of(NewsItem.class)
                .setParameter("author", "JohnSmith", "FooBar")
                .setParameter("newspaper", NYT, WAPO, LATIMES)
                .setParameter("year", "1970")
                .setParameter("category", BUSINESS, POLITICS, SPORTS);

        Subscriber<NewsItem> subscriber1 = Subscriber.<NewsItem>builder()
                .messageFilter(filter1)
                .consumer(newsItem -> System.out.println("Subscriber 1 received: " + newsItem))
                .build();

        subscriber1.bind(connection);

        // subscriber 2
        MessageFilter<NewsItem> filter2 = MessageFilter.of(NewsItem.class)
                .setParameter("author", "JohnSmith")
                .setParameter("newspaper", LATIMES)
                .setParameter("year", "1970")
                .setParameter("category", SCIENCE, SPORTS);

        Subscriber<NewsItem> subscriber2 = Subscriber.<NewsItem>builder()
                .messageFilter(filter2)
                .consumer(newsItem -> System.out.println("Subscriber 2 received: " + newsItem))
                .build();

        subscriber2.bind(connection);

        // publisher
        final Publisher<NewsItem> publisher = Publisher.builder(NewsItem.class)
                .addTypeAdapter(Date.class, date -> new SimpleDateFormat("yyyy").format(date))
                .build();

        publisher.bind(connection);

        // make sure to clean up on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    System.out.println("Shutting down...");
                    subscriber1.unbind();
                    subscriber2.unbind();
                    publisher.unbind();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // generate random messages
        while (true) {
            publisher.publish(generateNewsItem());
            Thread.sleep(500);
        }

    }


    private static NewsItem generateNewsItem() {
        return new NewsItem(randomNewspaper(),
                randomCategory(),
                randomAuthor(), new Date(0), "my title", "my content");
    }

    private static final List<String> NAMES = Arrays.asList("JohnSmith", "FooBar");
    private static final Random RANDOM = new Random();

    private static String randomAuthor() {
        return NAMES.get(RANDOM.nextInt(NAMES.size()));
    }

}
