package io.keyko.nevermined.cli.modules.assets;

import com.fasterxml.jackson.core.type.TypeReference;
import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DDOException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.asset.AssetMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "import",
        description = "Import an asset using the metadata in JSON format ")
public class AssetsImport implements Callable {

    private static final Logger log = LogManager.getLogger(AssetsImport.class);

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String metadataFile;

    CommandResult importAsset() throws CLIException {

        DDO ddo;
        try {
            command.println("Importing asset using file " + metadataFile);

            command.cli.progressBar.start();

            ddo = command.cli.getNeverminedAPI().getAssetsAPI()
                    .create(assetMetadataBuilder(metadataFile), command.serviceEndpointsBuilder());

            command.println("Asset Created: " + ddo.getDid().toString());

        } catch (IOException e) {
            command.printError("Error parsing metadata");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (DDOException e) {
            command.printError("Error with DDO");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult().setResult(ddo);

    }


    AssetMetadata assetMetadataBuilder(String filePath) throws IOException {
        String jsonContent =  new String(Files.readAllBytes(Paths.get(filePath)));
        return DDO.fromJSON(new TypeReference<AssetMetadata>() {}, jsonContent);
    }

    @Override
    public Object call() throws CLIException {
        return importAsset();
    }
}
