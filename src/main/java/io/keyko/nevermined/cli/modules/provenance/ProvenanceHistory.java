package io.keyko.nevermined.cli.modules.provenance;

import io.keyko.nevermined.cli.ProvenanceCommand;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.exceptions.ProvenanceException;
import io.keyko.nevermined.models.DID;
import io.keyko.nevermined.models.contracts.ProvenanceEvent;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;


@CommandLine.Command(
        name = "history",
        description = "Get the provenance history for a specific asset given a DID")
public class ProvenanceHistory implements Callable {

    @CommandLine.ParentCommand
    ProvenanceCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0")
    String didString;

    CommandResult provenanceMethod() throws CLIException {

        try {
            command.printHeader("Asset provenance history:");
            command.printSubHeader(didString);

            final List<ProvenanceEvent> provenanceEvents = command.cli.getNeverminedAPI().getProvenanceAPI()
                    .getDIDProvenanceEvents(new DID(didString));

            for (ProvenanceEvent event: provenanceEvents)   {
                command.print(event);
            }

        } catch (ProvenanceException | DIDFormatException e) {
            command.printError("Error reading provenance: " + e.getMessage());
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult();
    }

    @Override
    public Object call() throws CLIException {
        return provenanceMethod();
    }
}
