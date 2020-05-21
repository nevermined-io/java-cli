package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.exceptions.OrderException;
import io.keyko.nevermined.models.DID;
import io.keyko.nevermined.models.asset.OrderResult;
import io.reactivex.Flowable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "order",
        description = "Order an asset given DID")
public class AssetsOrder implements Callable {

    private static final Logger log = LogManager.getLogger(AssetsOrder.class);

    @CommandLine.ParentCommand
    AssetsCLI parent;

    @CommandLine.Spec
    public CommandLine.Model.CommandSpec spec;


    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Option(names = { "-s", "--serviceIndex" }, required = false, description = "service index to order")
    int serviceIndex = 0;

    CommandResult order() throws CLIException {

        OrderResult orderResult;
        try {
            parent.spec.commandLine().getOut().println("Ordering did " + did);

            parent.cli.progressBar.start();

            DID assetDid= new DID(did);

            Flowable<OrderResult> response = parent.cli.getNeverminedAPI().getAssetsAPI()
                    .order(assetDid, serviceIndex);

            orderResult = response.blockingFirst();

            if (null == orderResult.getServiceAgreementId()) {
                throw new CLIException("Unable to initialize order");
            }

            if (orderResult.isAccessGranted())
                parent.spec.commandLine().getOut().println("Access Granted. ServiceAgreementId: " + orderResult.getServiceAgreementId());
            else
                throw new CLIException("Access Not Granted. ServiceAgreementId: " + orderResult.getServiceAgreementId());


        } catch (DIDFormatException | OrderException e) {
            throw new CLIException(e.getMessage());
        } finally {
            parent.cli.progressBar.doStop();
        }
        return CommandResult.successResult().setResult(orderResult);

    }

    @Override
    public CommandResult call() throws CLIException {
        return order();
    }
}
