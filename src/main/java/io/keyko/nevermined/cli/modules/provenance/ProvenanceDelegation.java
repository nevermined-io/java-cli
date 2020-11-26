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
import picocli.CommandLine;

import java.util.concurrent.Callable;

import static io.keyko.nevermined.cli.helpers.CommandLineHelper.*;


@CommandLine.Command(
        name = "delegation",
        description = "Provenance registration of the delegation of an asset to a different agent for doing an activity")
public class ProvenanceDelegation implements Callable {

    @CommandLine.ParentCommand
    ProvenanceCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Option(names = { "-p", "--provenanceId" }, defaultValue = "",
            description = PARAM_PROVENANCE_ID_DESC)
    String provenanceId;

    @CommandLine.Option(names = { "-a", "--delegatedAgent" }, required = true,
            description = "The Ethereum address of the agent delegated")
    String delegatedAgent;

    @CommandLine.Option(names = { "-t", "--activity" }, defaultValue = "",
            description = PARAM_ACTIVITY_DESC)
    String activityId;

    @CommandLine.Option(names = { "-s", "--signature" }, defaultValue = "",
            description = "Signature of the provenance id given by the delegated agent")
    String signature;

    @CommandLine.Option(names = { "-i", "--attributes" }, defaultValue = "delegation",
            description = PARAM_ATTRIBUTES_DESC)
    String attributes;


    private void parseParameters() throws InputParseException {
        this.provenanceId = CommandLineHelper.generateProvenanceIdIfEmpty(provenanceId);
        this.activityId = CommandLineHelper.generateActivityIfEmpty(activityId, "delegation");
        if (!isAddressFormatValid(delegatedAgent))
            throw new InputParseException("Invalid agent address provided");
    }

    CommandResult provenanceMethod() throws CLIException {

        try {
            parseParameters();
            String responsibleAgentId = command.cli.getMainAddress();

            command.printHeader("Registering Delegation Provenance Entry:");
            command.printSubHeader(did);

            command.cli.progressBar.start();

            final boolean result = command.cli.getNeverminedAPI().getProvenanceAPI()
                    .actedOnBehalf(provenanceId, new DID(did), delegatedAgent, responsibleAgentId, activityId, signature, attributes);

            if (result)
                command.println("Provenance event registered. Provenance Id: " + provenanceId);
            else
                command.printError("Unable to register event with Provenance Id: " + provenanceId);

        } catch (InputParseException | DIDFormatException| ProvenanceException e) {
            command.printError("Error registering provenance: " + e.getMessage());
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
