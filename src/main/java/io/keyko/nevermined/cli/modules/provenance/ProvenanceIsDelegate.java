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
        name = "is-delegate",
        description = "Returns true or false if an address is a provenance delegate for a DID")
public class ProvenanceIsDelegate implements Callable {

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

            command.printHeader("Is a provenance delegate for the DID:");

            final boolean result = command.cli.getNeverminedAPI().getProvenanceAPI()
                    .isProvenanceDelegate(new DID(did), delegate);

            if (result) {
                command.println(NeverminedBaseCommand.SUCCESS_INDICATOR +
                        delegate + " is a delegate of " + did);
                return CommandResult.successResult().setMessage("true");
            } else {
                command.println(NeverminedBaseCommand.ERROR_INDICATOR +
                        delegate + " is not a delegate of " + did);
                return CommandResult.successResult().setMessage("false");
            }

        } catch (InputParseException | DIDFormatException| ProvenanceException e) {
            command.printError("Error managing delegate: " + e.getMessage());
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
        }
    }

    @Override
    public Object call() throws CLIException {
        return provenanceMethod();
    }
}
