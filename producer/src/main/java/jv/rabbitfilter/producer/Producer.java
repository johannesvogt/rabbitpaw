package jv.rabbitfilter.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jv.rabbitfilter.core.Envelope;
import jv.rabbitfilter.producer.converter.JsonConverter;

import java.io.IOException;

/**
 * Created by johannes on 15/08/15.
 */
public class Producer {

    public void publish(Object object) throws IOException, IllegalAccessException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(object.getClass().getName(),"topic");

        Envelope envelope = new JsonConverter().convert(object);

        channel.basicPublish(object.getClass().getName(), envelope.getRoutingKey(), null, envelope.getBytes());

    }
}
