# rabbitpaw

Annotation-based message router/filter for RabbitMQ

Rabbitpaw simplifies routing or filtering of RabbitMQ topic-messages. The routing-key is automatically created based on annotated fields in the Message-Class. The RabbitMQ - exchanges, queues and bindings are automatically created based on a `MessageFilter` - object.

Let's say we have a message-class, annotated with the `RoutingField` annotation:
```java
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
```java
Publisher<MyMessage> publisher = Publisher.builder(MyMessage.class)
                .addTypeAdapter(Date.class, date -> new SimpleDateFormat("yyyy").format(date))
                .build();

publisher.bind(connection);

publisher.publish(myMessage);
```
Where `myMessage` is an instance of the class `MyMessage`. When the value of category for `myMessage` is "books", and the value for date is 01/01/2015, then the routing key for this message will be "books.2015" . Note that the TypeAdapter that was added to the publisher defines how to translate objects to strings for the routing key. By default the `.toString()` method is used.

On the subscriber-side a `MessageFilter` is created to define to which Messages the subscriber listens to:
```java
        MessageFilter<MyMessage> filter = MessageFilter.of(MyMessage.class)
                .setParameter("category", "books", "electronics")
                .setParameter("date", "2012", "2013", "2014", "2015");

        Subscriber<MyMessage> subscriber = Subscriber.<MyMessage>builder()
                .messageFilter(filter)
                .consumer(myMessage -> System.out.println("Subscriber received: " + myMessage))
                .build();

        subscriber.bind(connection);
```
In this case the subscriber would receive all messages where category is "category" or "books" or "electronics", AND where date is between "2012" and "2015". The subscriber automatically creates a queue on the RabbitMQ-Server, plus the needed bindings (and if needed exchanges) that reflect the defined behavior.

In the above example the following bindings would be created:
```
Exchange1(publish point for MyMessage) ->(Bindings: 'books.*', 'electronics.*')-> Exchange2 ->(Bindings: '*.2012', '*.2013', '*.2014', '*.2015')-> Queue
```
