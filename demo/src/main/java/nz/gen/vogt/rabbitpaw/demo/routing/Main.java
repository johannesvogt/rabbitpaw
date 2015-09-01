package nz.gen.vogt.rabbitpaw.demo.routing;

import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nz.gen.vogt.rabbitpaw.core.MessageSerializer;
import nz.gen.vogt.rabbitpaw.demo.newsticker.NewsItem;
import nz.gen.vogt.rabbitpaw.demo.routing.MessageWrapper.WorkerMessage;
import nz.gen.vogt.rabbitpaw.publisher.Publisher;
import nz.gen.vogt.rabbitpaw.subscriber.MessageFilter;
import nz.gen.vogt.rabbitpaw.subscriber.Subscriber;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;


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
                .setParameter("year", "1970");

        Subscriber<NewsItem> subscriber1 = Subscriber.<NewsItem>builder()
                .messageFilter(filter1)
                .consumer(newsItem -> System.out.println("Subscriber 1 received: " + newsItem))
                .build();

        subscriber1.bind(connection);

        // subscriber 2
        MessageFilter<NewsItem> filter2 = MessageFilter.of(NewsItem.class)
                .setParameter("author", "JohnSmith")
                .setParameter("year", "1970");

        Subscriber<NewsItem> subscriber2 = Subscriber.<NewsItem>builder()
                .messageFilter(filter2)
                .consumer(newsItem -> System.out.println("Subscriber 2 received: " + newsItem))
                .build();

        subscriber2.bind(connection);

        // publisher
        final Publisher<MessageWrapper> publisher = Publisher.builder(MessageWrapper.class)
                .serializer(new MessageSerializer<MessageWrapper>() {
                    @Override
                    public byte[] serialize(MessageWrapper messageWrapper) throws IOException {
                        return new Gson().toJson(messageWrapper).getBytes("UTF-8");
                    }
                })
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
            publisher.publish(wrapMessage(generateWorkerMessage(), "type", "domain", "recipient"));
            Thread.sleep(500);
        }

    }

    private static MessageWrapper wrapMessage(WorkerMessage workerMessage, String type, String domain, String recipient) {
        return new MessageWrapper(type, domain, recipient, workerMessage);
    }

    private static WorkerMessage generateWorkerMessage() {
        return new WorkerMessageTypeA("my content");
    }


}
