package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.CarContract;
import com.template.states.CarState;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import javax.sql.rowset.spi.TransactionalWriter;

import static com.template.contracts.CarContract.CAR_CONTRACT_ID;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class shipmentFlow extends FlowLogic<Void> {
    private String model;
    private  Party owner;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public shipmentFlow(String model, Party owner) {
        this.model = model;
        this.owner = owner;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        // Initiator flow logic goes here.

        //retrieve notary identity from network map
     Party Notary= getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // create transactioncomponents [input/output]
        CarState outputState= new CarState(model,owner,getOurIdentity());

        //create transactionbuilder and add components
        TransactionBuilder txBuilder =new TransactionBuilder(Notary)
                .addOutputState(outputState,CAR_CONTRACT_ID)
                .addCommand(new CarContract.Shipment(),getOurIdentity().getOwningKey());

        //signing transaction
        SignedTransaction shipmentTx= getServiceHub().signInitialTransaction(txBuilder);
        //create session with counterparty
        FlowSession otherPartySession = initiateFlow(owner);


        //finalizing the transaction
       subFlow(new FinalityFlow(shipmentTx,otherPartySession));

        return null;
    }
}
