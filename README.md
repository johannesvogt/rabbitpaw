# rabbitpaw

Annotation-based message router/filter for RabbitMQ

Rabbitpaw automates routing or filtering of RabbitMQ topic-messages based on annotations. RabbitMQs topic-routing is a very performant way of dispatching messages to many different Consumers (see also [Very fast and scalable topic routing](https://www.rabbitmq.com/blog/2010/09/14/very-fast-and-scalable-topic-routing-part-1/)). 

Rabbitpaw creates routing-keys of messages based on annotated fields of the Message-Class. The exchanges, queues and bindings are automatically created based on a `MessageFilter` - object.

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

MyMessage myMessage = new MyMessage("books", new Date(1420070400), otherContent);
publisher.publish(myMessage);
```
When `category` has the value "books", and `date` is 01/01/2015, then the routing key for this message will be "books.2015" . The TypeAdapter added to the publisher defines how to translate objects to routing-strings. By default the `.toString()` method is used for all objects.

On the subscriber-side a `MessageFilter` is created to define to which Messages a subscriber listens to:
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
In this case the subscriber would receive all messages where category is "books" or "electronics", AND where date is between "2012" and "2015". The subscriber automatically creates a queue on the RabbitMQ-Server, plus the needed bindings (and if needed additional exchanges) that reflect the defined behavior.

In the above example the following bindings would be created:
```
Exchange1(publish point for MyMessage) ->(Bindings: 'books.*', 'electronics.*')-> Exchange2 ->(Bindings: '*.2012', '*.2013', '*.2014', '*.2015')-> Queue
```

The connection to the RabbitMQ - Server are passed to `Publisher` and to `Subscriber` via the `.bind` - methods. For more detailed examples see the demos.

