package io.keyko.nevermined.cli.modules.contract;

import io.keyko.nevermined.cli.NetworkCommand;
import io.keyko.nevermined.cli.helpers.ContractsReflections;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "describe",
        description = "Describe a Keeper contract")
public class ContractDescribe implements Callable {

    @CommandLine.ParentCommand
    NetworkCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0", arity = "1")
    String contractName= null;


    CommandResult describe() {

        try {

            if (!ContractsReflections.contractExists(contractName)) {
                command.printError("The Contract name provided doesn't exist: " + contractName);
                return CommandResult.errorResult();
            }

            command.println("\n@|bold,blue,underline Description of Contract:|@\n");

            Class clazz= ContractsReflections.getContractClass(contractName);
            ContractsReflections.printClassInformation(clazz, command.getOut());

        } catch (Exception e) {
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        }

        return CommandResult.successResult();
    }



    @Override
    public Object call() {
        return describe();
    }
}
