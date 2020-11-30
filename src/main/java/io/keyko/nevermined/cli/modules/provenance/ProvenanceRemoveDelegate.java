package io.keyko.nevermined.cli.modules.provenance;

import io.keyko.nevermined.cli.NeverminedBaseCommand;
import io.keyko.nevermined.cli.ProvenanceCommand;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.cli.models.exceptions.InputParseException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.exceptions.ProvenanceException;
import io.keyko.nevermined.models.DID;
import picocli.CommandLine;

import java.util.concurrent.Callable;

import static io.keyko.nevermined.cli.helpers.CommandLineHelper.isAddressFormatValid;


@CommandLine.Command(
        name = "remove-delegate",
        description = "Remove an address as provenance delegate of a DID")
public class ProvenanceRemoveDelegate implements Callable {

    @CommandLine.ParentCommand
    ProvenanceCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0")
    String delegate;

    @CommandLine.Parameters(index = "1")
    String did;

    private void parseParameters() throws InputParseException {
        if (!isAddressFormatValid(delegate))
            throw new InputParseException("Invalid agent address provided");
    }

    CommandResult provenanceMethod() throws CLIException {

        try {
            parseParameters();

            command.printHeader("Removing address of the delegates list:");

            command.cli.progressBar.start();

            final boolean result = command.cli.getNeverminedAPI().getProvenanceAPI()
                    .removeDIDProvenanceDelegate(new DID(did), delegate);

            if (result) {
                command.println(NeverminedBaseCommand.SUCCESS_INDICATOR + "Delegate removed");
                return CommandResult.successResult();
            } else {
                command.printError("Unable to remove delegate to the DID provided");
                return CommandResult.errorResult();
            }

        } catch (InputParseException | DIDFormatException| ProvenanceException e) {
            command.printError("Error managing delegate: " + e.getMessage());
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
    }

    @Override
    public Object call() throws CLIException {
        return provenanceMethod();
    }
}
