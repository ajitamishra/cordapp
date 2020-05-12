package com.template.contracts;

import com.template.states.CarState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

// ************
// * Contract *
// ************
public class CarContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String CAR_CONTRACT_ID = "com.template.contracts.CarContract";


    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {
        if(tx.getCommands().size()!=1) throw new IllegalArgumentException("There can be only one command");
        Command command = tx.getCommand(0);
        CommandData commandType= command.getValue();
        List<PublicKey> requiredSigners=command.getSigners();

        if(commandType instanceof Shipment){

            //shape
          if(tx.getInputStates().size()!=0) throw new IllegalArgumentException("There can not be any input");
          if(tx.getOutputStates().size()!=1) throw new IllegalArgumentException("There must be only one ouput");


            //content
            ContractState outputState= tx.getOutput(0);
            if(!(outputState instanceof CarState)) throw new IllegalArgumentException("outputstate has to be instance of CarState");


            CarState carState=(CarState) outputState;
            if(!(carState.getModel().equals("CyberTruck"))) throw new IllegalArgumentException("This is not a cyber Truck");

            //signers

            PublicKey manufacturerKey= carState.getManufacturer().getOwningKey();
            if(!(requiredSigners.contains(manufacturerKey))) throw  new IllegalArgumentException("Manufacturer must sign the transaction");
        }


    }

    // Used to indicate the transaction's intent.
    public static class Shipment implements  CommandData{}
}