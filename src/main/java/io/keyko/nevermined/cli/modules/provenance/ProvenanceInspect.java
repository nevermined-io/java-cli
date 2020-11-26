package io.keyko.nevermined.cli.modules.provenance;

import io.keyko.nevermined.cli.ProvenanceCommand;
import io.keyko.nevermined.cli.helpers.CommandLineHelper;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.cli.models.exceptions.InputParseException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.exceptions.ProvenanceException;
import io.keyko.nevermined.models.DID;
import io.keyko.nevermined.models.contracts.ProvenanceEntry;
import picocli.CommandLine;

import java.util.concurrent.Callable;

import static io.keyko.nevermined.cli.helpers.CommandLineHelper.*;


@CommandLine.Command(
        name = "inspect",
        description = "Fetch the on-chain information regarding a Provenance Id")
public class ProvenanceInspect implements Callable {

    @CommandLine.ParentCommand
    ProvenanceCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0")
    String provenanceId;

    CommandResult provenanceMethod() throws CLIException {

        try {
            command.printHeader("Inspecting on-chain provenance:");
            command.printSubHeader(provenanceId);

            command.cli.progressBar.start();

            final ProvenanceEntry entry = command.cli.getNeverminedAPI().getProvenanceAPI()
                    .getProvenanceEntry(provenanceId);

            if (null == entry)
                command.printError("Unable to register event with Provenance Id: " + provenanceId);
            else    {
                command.println("\tProvenance ID:   \t" + provenanceId);
                command.println("\tDID:             \t" + entry.did.getDid());
                command.println("\tRelated DID:     \t" + entry.relatedDid.getDid());
                command.println("\tCreated by:      \t" + entry.createdBy);
                command.println("\tAgent:           \t" + entry.agentId);
                command.println("\tAgent Involved:  \t" + entry.agentInvolvedId);
                command.println("\tActivity:        \t" + entry.activityId);
                command.println("\tW3C PROV Method: \t" + entry.method.name());
                command.println("\tSignature:       \t" + entry.signature);
                command.println("\tBlock number:    \t" + entry.blockNumberUpdated.toString());

            }

        } catch (ProvenanceException e) {
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
