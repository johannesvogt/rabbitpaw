package nz.gen.vogt.rabbitpaw.demo.routing;

import nz.gen.vogt.rabbitpaw.core.annotation.Message;
import nz.gen.vogt.rabbitpaw.core.annotation.RoutingField;

/**
 * Created by Johannes Vogt on 30/08/15.
 */
@Message(exchangeName = "application.worker")
public class MessageWrapper {

    @RoutingField
    private String type;

    @RoutingField
    private String domain;

    @RoutingField
    private String recipient;

    private WorkerMessage workerMessage;

    public MessageWrapper(String type, String domain, String recipient, WorkerMessage workerMessage) {
        this.type = type;
        this.domain = domain;
        this.recipient = recipient;
        this.workerMessage = workerMessage;
    }

    public static interface WorkerMessage {

    }

}
