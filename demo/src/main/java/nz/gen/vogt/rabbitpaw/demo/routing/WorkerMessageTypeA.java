package nz.gen.vogt.rabbitpaw.demo.routing;

import nz.gen.vogt.rabbitpaw.demo.routing.MessageWrapper.WorkerMessage;

/**
 * Created by Johannes Vogt on 30/08/15.
 */
public class WorkerMessageTypeA implements WorkerMessage {

    private String typeAContent;

    public WorkerMessageTypeA(String typeAContent) {
        this.typeAContent = typeAContent;
    }
}
