package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.*;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.DID;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "download-my-asset",
        description = "Download an asset owned by me.\n " +
                "It supports downloading a previously purchase asset (passing the --serviceAgrementId flag)\n" +
                "or order the asset and download it if that is not given")
public class AssetsDownloadMyAsset implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Option(names = { "-i", "--serviceIndex" }, description = "service index to consume", defaultValue = "-1")
    Integer serviceIndex = -1;

    @CommandLine.Option(names = { "-p", "--path" }, description = "path where to download the asset")
    String path= "";

    CommandResult getMyAsset() throws CLIException {
        try {
            if (null == path || path.isEmpty())
                path= command.cli.getMainConfig().getString("consume.basePath");

            command.printHeader("Downloading asset files: ");
            command.printSubHeader(did);

            DID assetDid= new DID(did);

            command.cli.progressBar.start();

            if (serviceIndex < 0)  {
                DDO assetDdo = command.cli.getNeverminedAPI().getAssetsAPI().resolve(assetDid);
                serviceIndex = assetDdo.getAccessService().index;
            }

            Boolean status = command.cli.getNeverminedAPI().getAssetsAPI()
                    .ownerDownload(assetDid, serviceIndex, path);

            if (status) {
                command.printSuccess();
                command.println("File/s downloaded to " + command.getItem(path));
            } else  {
                command.printError("Unable to download file/s to " + path);
                return CommandResult.errorResult();
            }

        } catch (DIDFormatException | ConsumeServiceException | DDOException | EthereumException e) {
            command.printError("Unable to get to files");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (ServiceException e) {
            command.printError("Unable to order asset");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult();
    }


    @Override
    public CommandResult call() throws CLIException {
        return getMyAsset();
    }
}
