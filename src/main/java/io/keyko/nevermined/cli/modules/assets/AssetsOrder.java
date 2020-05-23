package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.exceptions.OrderException;
import io.keyko.nevermined.models.DID;
import io.keyko.nevermined.models.asset.OrderResult;
import io.reactivex.Flowable;
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

    @CommandLine.Option(names = { "-s", "--serviceIndex" }, required = false, description = "service index to order")
    int serviceIndex = 0;

    CommandResult order() throws CLIException {

        OrderResult orderResult;
        try {
            command.println("Ordering did " + did);

            command.cli.progressBar.start();

            DID assetDid= new DID(did);

            Flowable<OrderResult> response = command.cli.getNeverminedAPI().getAssetsAPI()
                    .order(assetDid, serviceIndex);

            orderResult = response.blockingFirst();

            if (null == orderResult.getServiceAgreementId()) {
                throw new CLIException("Unable to initialize order");
            }

            if (orderResult.isAccessGranted())
                command.println("Access Granted. ServiceAgreementId: " + orderResult.getServiceAgreementId());
            else
                throw new CLIException("Access Not Granted. ServiceAgreementId: " + orderResult.getServiceAgreementId());


        } catch (DIDFormatException | OrderException e) {
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
