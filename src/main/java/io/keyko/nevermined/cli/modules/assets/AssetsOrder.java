package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.exceptions.EscrowRewardException;
import io.keyko.nevermined.exceptions.OrderException;
import io.keyko.nevermined.exceptions.ServiceException;
import io.keyko.nevermined.models.DID;
import io.keyko.nevermined.models.asset.OrderResult;
import picocli.CommandLine;

import java.util.concurrent.Callable;


@CommandLine.Command(
        name = "order",
        description = "Order an asset given DID")
public class AssetsOrder implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Option(names = { "-s", "--serviceIndex" }, description = "service index to order")
    int serviceIndex = 0;

    CommandResult order() throws CLIException {

        OrderResult orderResult;
        try {
            command.println("Ordering Asset: " + did);

            command.cli.progressBar.start();

            DID assetDid= new DID(did);

            orderResult = command.cli.getNeverminedAPI().getAssetsAPI()
                    .orderDirect(assetDid, serviceIndex);

            if (!orderResult.isAccessGranted()) {
                command.printError("Access not granted");
                return CommandResult.errorResult();
            }

            if (orderResult.isAccessGranted())
                command.println("Access Granted. ServiceAgreementId: " + orderResult.getServiceAgreementId());
            else {
                command.printError("Access not granted");
                return CommandResult.errorResult();
            }

        } catch (DIDFormatException | OrderException | EscrowRewardException | ServiceException e) {
            command.printError("Error ordering asset");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult().setResult(orderResult);

    }

    @Override
    public CommandResult call() throws CLIException {
        return order();
    }
}
