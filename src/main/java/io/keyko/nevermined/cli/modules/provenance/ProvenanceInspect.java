package io.keyko.nevermined.cli.modules.provenance;

import io.keyko.nevermined.cli.ProvenanceCommand;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.ProvenanceException;
import io.keyko.nevermined.models.contracts.ProvenanceEntry;
import picocli.CommandLine;

import java.util.concurrent.Callable;


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

    @CommandLine.Option(names = { "-s", "--skipSignatureValidation" }, defaultValue = "false",
            description = "If given, it skips the validation of the on-chain signatures ")
    boolean skipSignatureValidation;

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
                command.print(provenanceId, entry, skipSignatureValidation);
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
