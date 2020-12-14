package io.keyko.nevermined.cli.modules.compute;

import io.keyko.nevermined.cli.ComputeCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.ServiceException;
import picocli.CommandLine;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@CommandLine.Command(
        name = "status",
        description = "Get the current status of a compute job")
public class ComputeStatus implements Callable {

    @CommandLine.ParentCommand
    ComputeCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Option(names = { "-s", "--serviceAgreementId" }, required = true, description = "service agreement id")
    String serviceAgreementId;

    @CommandLine.Option(names = { "-e", "--executionId" }, required = true, description = "execution id")
    String executionId;

    @CommandLine.Option(names = {"-f", "--follow"}, description = "wait for job to finish")
    boolean follow;

    CommandResult status() throws CLIException, InterruptedException {
        command.printHeader("Getting current status for compute job: " + executionId);
        command.cli.progressBar.start();

        io.keyko.nevermined.models.gateway.ComputeStatus computeStatus;
         do {
            try {
                computeStatus = command.cli.getNeverminedAPI().getAssetsAPI()
                        .getComputeStatus(serviceAgreementId, executionId, command.serviceEndpointsBuilder());
            } catch (ServiceException e) {
                logger.debug(e.getMessage());
                command.printError("Unable to get status");
                return CommandResult.errorResult();
            }
            command.printStatusRunning(executionId + ": " + computeStatus.status);

            if (follow) {
                TimeUnit.SECONDS.sleep(5);
            }
        } while (follow && !(computeStatus.status.equals("Succeeded") || computeStatus.status.equals("Failed")));

        if (computeStatus.status.equals("Succeeded")) {
            command.printStatusSucceeded(executionId + ": " + computeStatus.status);
        } else if (computeStatus.status.equals("Failed")) {
            command.printStatusFailed(executionId + ": " + computeStatus.status);
        } else {
            command.printStatusRunning(executionId + ": " + computeStatus.status);
        }
        command.println();

        command.cli.progressBar.doStop();
        command.printSuccess();
        command.println("Output: " + computeStatus.did);
        return CommandResult.successResult();
    }

    @Override
    public Object call() throws CLIException, InterruptedException {
        return status();
    }
}
