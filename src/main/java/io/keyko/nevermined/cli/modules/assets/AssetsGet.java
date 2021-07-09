package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.*;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.DID;
import io.keyko.nevermined.models.asset.OrderResult;
import io.keyko.nevermined.models.service.Service;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "get",
        description = "Order & download or download directly a previously purchased asset.\n " +
                "It supports downloading a previously purchase asset (passing the --serviceAgrementId flag)\n" +
                "or order the asset and download it if that is not given")
public class AssetsGet implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Option(names = { "-s", "--serviceAgreementId" }, description = "service agreement id", defaultValue = "")
    String serviceAgreementId;

    @CommandLine.Option(names = { "-i", "--serviceIndex" }, description = "service index to consume", defaultValue = "-1")
    Integer serviceIndex = -1;

    @CommandLine.Option(names = { "-f", "--fileIndex" }, description = "file index to download. if not given will download all the files", defaultValue = "-1")
    Integer fileIndex = -1;

    @CommandLine.Option(names = { "-p", "--path" }, description = "path where to download the asset")
    String path= "";

    CommandResult get() throws CLIException {
        try {
            if (null == path || path.isEmpty())
                path= command.cli.getMainConfig().getString("consume.basePath");

            command.printHeader("Downloading asset files: ");
            command.printSubHeader(did);

            DID assetDid= new DID(did);

            command.cli.progressBar.start();


            if (null == serviceAgreementId || serviceAgreementId.length() <1)   {

                OrderResult orderResult;

                if (serviceIndex >= 0)
                    orderResult = command.cli.getNeverminedAPI().getAssetsAPI()
                            .order(assetDid, serviceIndex);
                else {
                    orderResult = command.cli.getNeverminedAPI().getAssetsAPI()
                            .order(assetDid, Service.ServiceTypes.ACCESS);
                    serviceIndex = orderResult.getServiceIndex();
                }
                serviceAgreementId = orderResult.getServiceAgreementId();
                command.println("DID Ordered, Service Agreement Id: " + serviceAgreementId);
            }

            if (serviceIndex < 0)  {
                DDO assetDdo = command.cli.getNeverminedAPI().getAssetsAPI().resolve(assetDid);
                serviceIndex = assetDdo.getAccessService().index;
            }

            Boolean status = false;
            if (fileIndex >= 0)
                status = command.cli.getNeverminedAPI().getAssetsAPI()
                        .download(serviceAgreementId, assetDid, serviceIndex, fileIndex, path);
            else
                status = command.cli.getNeverminedAPI().getAssetsAPI()
                        .download(serviceAgreementId, assetDid, serviceIndex, path);

            if (status) {
                command.printSuccess();
                command.println("File/s downloaded to " + command.getItem(path));
            } else  {
                command.printError("Unable to download file/s to " + path);
                return CommandResult.errorResult();
            }

        } catch (DownloadServiceException | DIDFormatException | DDOException | EthereumException e) {
            command.printError("Unable to get to files");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (ServiceException | OrderException | EscrowPaymentException e) {
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
        return get();
    }
}
