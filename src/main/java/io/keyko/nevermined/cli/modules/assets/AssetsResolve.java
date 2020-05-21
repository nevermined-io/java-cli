package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.cli.helpers.CommandLineHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.keyko.nevermined.exceptions.DDOException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.exceptions.EthereumException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.DID;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "resolve",
        description = "Resolve an asset using a given DID")
public class AssetsResolve implements Callable {

    @CommandLine.ParentCommand
    AssetsCLI parent;

    @CommandLine.Spec
    public CommandLine.Model.CommandSpec spec;


    @CommandLine.Parameters(index = "0")
    String did;

    CommandResult resolve() throws CLIException {

        DDO ddo;
        try {
            parent.spec.commandLine().getOut().println("Resolving " + did);

            parent.cli.progressBar.start();

            ddo = parent.cli.getNeverminedAPI().getAssetsAPI()
                    .resolve(new DID(did));

            parent.spec.commandLine().getOut().println();
            parent.spec.commandLine().getOut().println(CommandLineHelper.prettyJson(ddo.getMetadataService().toJson()));

        } catch (DDOException | DIDFormatException | EthereumException | JsonProcessingException e) {
            throw new CLIException("Error resolving DDO: " + e.getMessage());
        } finally {
            parent.cli.progressBar.doStop();
        }
        return CommandResult.successResult().setResult(ddo);
    }

    @Override
    public Object call() throws CLIException {
        return resolve();
    }
}
