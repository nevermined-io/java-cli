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
        description = "Download a previously ordered asset given a DID")
public class AssetsGet implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Option(names = { "-a", "--serviceAgreementId" }, required = false, description = "service agreement id", defaultValue = "")
    String serviceAgreementId;

    @CommandLine.Option(names = { "-s", "--serviceIndex" }, required = false, description = "service index to consume", defaultValue = "-1")
    String serviceIndex = "-1";



    @CommandLine.Option(names = { "-p", "--path" }, required = false, description = "path where to download the asset")
    String path= "";

    CommandResult get() throws CLIException {
        try {
            if (null == path || path.isEmpty())
                path= command.cli.getMainConfig().getString("consume.basePath");

            command.printHeader("Downloading asset: ");
            command.printSubHeader(did);

            DID assetDid= new DID(did);

            command.cli.progressBar.start();

            int index = Integer.parseInt(serviceIndex);

            if (null == serviceAgreementId || serviceAgreementId.length() <1)   {

                OrderResult orderResult;

                if (index >= 0)
                    orderResult = command.cli.getNeverminedAPI().getAssetsAPI()
                        .orderDirect(assetDid, index);
                else {
                    orderResult = command.cli.getNeverminedAPI().getAssetsAPI()
                            .orderDirect(assetDid, Service.ServiceTypes.ACCESS);
                    index = orderResult.getServiceIndex();
                }
                serviceAgreementId = orderResult.getServiceAgreementId();
                command.println("DID Ordered, Service Agreement Id: " + serviceAgreementId);
            }

            if (index < 0)  {
                DDO assetDdo = command.cli.getNeverminedAPI().getAssetsAPI().resolve(assetDid);
                index = assetDdo.getAccessService().index;
            }

            Boolean status = command.cli.getNeverminedAPI().getAssetsAPI()
                    .consume(serviceAgreementId, assetDid, index, path);

            if (status) {
                command.printSuccess();
                command.println("Files downloaded to " + command.getItem(path));
            } else  {
                command.printError("Unable to download files to " + path);
                return CommandResult.errorResult();
            }

        } catch (DIDFormatException | ConsumeServiceException | DDOException | EthereumException e) {
            command.printError("Unable to get to files");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (ServiceException | OrderException | EscrowRewardException e) {
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
