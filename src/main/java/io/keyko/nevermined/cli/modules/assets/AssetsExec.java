package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.*;
import io.keyko.nevermined.external.GatewayService;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.DID;
import io.keyko.nevermined.models.asset.OrderResult;
import io.keyko.nevermined.models.service.Service;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "exec",
        description = "Execute a compute service given a DID")
public class AssetsExec implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Option(names = { "-w", "--workflow" }, required = true, description = "workflow DID")
    String workflowId;

    @CommandLine.Option(names = { "-s", "--serviceAgreementId" }, description = "service agreement id", defaultValue = "")
    String serviceAgreementId;

    @CommandLine.Option(names = { "-i", "--serviceIndex" }, description = "service index to execute", defaultValue = "-1")
    String serviceIndex = "-1";


    CommandResult get() throws CLIException {
        OrderResult orderResult;

        try {
            command.printHeader("Executing asset: " + did);

            DID assetDid= new DID(did);
            DID workflowDid= new DID(workflowId);

            command.cli.progressBar.start();

            int index = Integer.parseInt(serviceIndex);

            if (null == serviceAgreementId || serviceAgreementId.length() <1)   {

                if (index >= 0)
                    orderResult = command.cli.getNeverminedAPI().getAssetsAPI()
                            .orderDirect(assetDid, index);
                else {
                    orderResult = command.cli.getNeverminedAPI().getAssetsAPI()
                            .orderDirect(assetDid, Service.ServiceTypes.COMPUTE);
                    index = orderResult.getServiceIndex();
                }
                serviceAgreementId = orderResult.getServiceAgreementId();
                command.println("DID Ordered, Service Agreement Id: " + serviceAgreementId);
            }

            if (index < 0)  {
                DDO assetDdo = command.cli.getNeverminedAPI().getAssetsAPI().resolve(assetDid);
                index = assetDdo.getComputeService().index;
            }

            final GatewayService.ServiceExecutionResult executionResult = command.cli.getNeverminedAPI()
                    .getAssetsAPI()
                    .execute(serviceAgreementId, assetDid, index, workflowDid);

            if (executionResult.getOk()) {
                command.printSuccess();
                command.println("Execution triggered: " + executionResult.getExecutionId());
                return CommandResult.successResult().setResult(executionResult);
            } else  {
                command.printError("Unable to trigger execution");
                return CommandResult.errorResult();
            }

        } catch (DIDFormatException | DDOException | EthereumException e) {
            command.printError("Unable to execute service");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (ServiceException | OrderException | EscrowRewardException e) {
            command.printError("Unable to order asset");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
    }


    @Override
    public CommandResult call() throws CLIException {
        return get();
    }
}
