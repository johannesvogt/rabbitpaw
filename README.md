# rabbitpaw

Annotation-based message router/filter for RabbitMQ

Rabbitpaw simplifies routing or filtering of RabbitMQ topic-messages. The routing-key is automatically created based on annotated fields in the Message-Class. The RabbitMQ - exchanges, queues and bindings are automatically created based on a `MessageFilter` - object.
