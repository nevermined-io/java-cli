package io.keyko.nevermined.cli.modules.compute;

import io.keyko.nevermined.cli.ComputeCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.ServiceException;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "logs",
        description = "Fetch the logs of a compute job")
public class ComputeLogs implements Callable {

    @CommandLine.ParentCommand
    ComputeCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Option(names = { "-s", "--serviceAgreementId"}, required = true, description = "service agreement id")
    String serviceAgreementId;

    @CommandLine.Option(names = { "-e", "--executionId" }, required = true, description = "execution id")
    String executionId;

    CommandResult logs() throws CLIException {
        command.printHeader("Fetching logs for compute job: " + executionId);
        command.cli.progressBar.start();

        List<io.keyko.nevermined.models.gateway.ComputeLogs> computeLogs;
        try {
            computeLogs = command.cli.getNeverminedAPI().getAssetsAPI()
                    .getComputeLogs(serviceAgreementId, executionId, command.serviceEndpointsBuilder());
        } catch (ServiceException e) {
            logger.debug(e.getMessage());
            command.printError("Unable to fetch logs: " + e.getMessage());
            return CommandResult.errorResult();
        }
        command.cli.progressBar.doStop();

        computeLogs.stream().forEach( line -> {
            command.println(line.podName + "\t|" + line.content);
        });
        command.println();

        command.printSuccess();
        return CommandResult.successResult();
    }

    @Override
    public Object call() throws CLIException {
        return logs();
    }
}
