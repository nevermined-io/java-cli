package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.ConsumeServiceException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.models.DID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "consume",
        description = "Download a previously ordered asset given a DID")
public class AssetsConsume implements Callable {

    private static final Logger log = LogManager.getLogger(AssetsConsume.class);

    @CommandLine.ParentCommand
    AssetsCLI parent;

    @CommandLine.Spec
    public CommandLine.Model.CommandSpec spec;


    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Option(names = { "-a", "--serviceAgreementId" }, required = true, description = "service agreement id")
    String serviceAgreementId;

    @CommandLine.Option(names = { "-s", "--serviceIndex" }, required = false, description = "service index to consume")
    int serviceIndex = 0;


    @CommandLine.Option(names = { "-p", "--path" }, required = false, description = "path where to download the asset")
    String path= "";

    CommandResult consume() throws CLIException {
        try {
            if (null == path || path.isEmpty())
                path= parent.cli.getMainConfig().getString("consume.basePath");

            parent.spec.commandLine().getOut().println("Downloading asset with DID " + did);

            DID assetDid= new DID(did);

            parent.cli.progressBar.start();

            Boolean status = parent.cli.getNeverminedAPI().getAssetsAPI()
                    .consume(serviceAgreementId, assetDid, serviceIndex, path);

            if (status)
                parent.spec.commandLine().getOut().println("Files downloaded to " + path);
            else
                throw new CLIException("Unable to download files to " + path);

        } catch (DIDFormatException | ConsumeServiceException e) {
            throw new CLIException(e.getMessage());
        } finally {
            parent.cli.progressBar.doStop();
        }
        return CommandResult.successResult();
    }


    @Override
    public CommandResult call() throws CLIException {
        return consume();
    }
}
