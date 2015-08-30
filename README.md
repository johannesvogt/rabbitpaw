# rabbitpaw

Annotation-based message router/filter for RabbitMQ

Rabbitpaw simplifies routing or filtering of RabbitMQ topic-messages. The routing-key is automatically created based on annotated fields in the Message-Class. The RabbitMQ - exchanges, queues and bindings are automatically created based on a `MessageFilter` - object.

Let's say we have a message-class, annotated with the `RoutingField` annotation:
```
public class MyMessage {

    @RoutingField
    private String category;

    @RoutingField
    private Date date;

    private String otherContent;
    ....
}
```
Then the rabbitpaw `Publisher` will create a routing-key for messages of that type according to the annotated fields:
```
Publisher<MyMessage> publisher = Publisher.builder(MyMessage.class)
                .addTypeAdapter(Date.class, date -> new SimpleDateFormat("yyyy").format(date))
                .build();

publisher.publish(myMessage);
```

