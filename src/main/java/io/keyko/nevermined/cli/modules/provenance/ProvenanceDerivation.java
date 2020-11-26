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
        name = "derivation",
        description = "Provenance registration of the derivation of a new asset using an existing one")
public class ProvenanceDerivation implements Callable {

    @CommandLine.ParentCommand
    ProvenanceCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Option(names = { "-n", "--newAsset" }, required = true,
            description = "DID of the new asset derived (example: did:nv:1234)")
    String newAssetDID;

    @CommandLine.Option(names = { "-d", "--derivedFrom" }, required = true,
            description = "DID of the new asset derived")
    String derivedFromDID;

    @CommandLine.Option(names = { "-p", "--provenanceId" }, defaultValue = "",
            description = PARAM_PROVENANCE_ID_DESC)
    String provenanceId;

    @CommandLine.Option(names = { "-a", "--agent" }, required = true,
            description = PARAM_AGENT_DESC)
    String agentId;

    @CommandLine.Option(names = { "-t", "--activity" }, defaultValue = "",
            description = PARAM_ACTIVITY_DESC)
    String activityId;

    @CommandLine.Option(names = { "-i", "--attributes" }, defaultValue = "derivation",
            description = PARAM_ATTRIBUTES_DESC)
    String attributes;


    private void parseParameters() throws InputParseException {
        this.provenanceId = CommandLineHelper.generateProvenanceIdIfEmpty(provenanceId);
        this.activityId = CommandLineHelper.generateActivityIfEmpty(activityId, "derivation");
        if (!isAddressFormatValid(agentId))
            throw new InputParseException("Invalid agent address provided");
    }

    CommandResult provenanceMethod() throws CLIException {

        try {
            parseParameters();

            command.printHeader("Registering Derivation Provenance Entry:");
            command.printSubHeader(newAssetDID);

            command.cli.progressBar.start();

            final boolean result = command.cli.getNeverminedAPI().getProvenanceAPI()
                    .wasDerivedFrom(provenanceId, new DID(newAssetDID), new DID(derivedFromDID), agentId, activityId, attributes);

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
