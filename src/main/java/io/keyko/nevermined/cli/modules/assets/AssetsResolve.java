package io.keyko.nevermined.cli.modules.assets;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.helpers.CommandLineHelper;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
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
    AssetsCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    CommandResult resolve() throws CLIException {

        DDO ddo;
        try {
            command.println("Resolving " + did);

            command.cli.progressBar.start();

            ddo = command.cli.getNeverminedAPI().getAssetsAPI()
                    .resolve(new DID(did));

            command.println();
            command.println(CommandLineHelper.prettyJson(ddo.getMetadataService().toJson()));

        } catch (DDOException | DIDFormatException | EthereumException | JsonProcessingException e) {
            command.printError("Error resolving asset");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult().setResult(ddo);
    }

    @Override
    public Object call() throws CLIException {
        return resolve();
    }
}
