package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;

// ******************
// * Responder flow *
// ******************
@InitiatedBy(shipmentFlow.class)
public class receiveShipmentFlow extends FlowLogic<Void> {
    private FlowSession otherpartySession;

    public receiveShipmentFlow(FlowSession otherpartySession) {
        this.otherpartySession = otherpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        // Responder flow logic goes here.
  subFlow(new ReceiveFinalityFlow(otherpartySession));
        return null;
    }
}
