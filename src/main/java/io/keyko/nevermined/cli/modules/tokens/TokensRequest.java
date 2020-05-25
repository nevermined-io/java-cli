package io.keyko.nevermined.cli.modules.tokens;

import io.keyko.nevermined.cli.TokensCommand;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.EthereumException;
import picocli.CommandLine;

import java.math.BigInteger;
import java.util.concurrent.Callable;

import static io.keyko.nevermined.cli.helpers.Constants.TRANSACTION_SUCCESS;

@CommandLine.Command(
        name = "request",
        description = "Request some Tokens")
public class TokensRequest implements Callable {

    @CommandLine.ParentCommand
    TokensCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0")
    BigInteger numberTokens;

    CommandResult request() throws CLIException {
        try {
            command.printHeader("Requesting Tokens:");
            command.println("Requesting " + numberTokens.longValue() +
                    " Token/s for " + command.cli.getNeverminedAPI().getMainAccount().getAddress() +
                    " address");

            command.cli.progressBar.start();

            String status= command.cli.getNeverminedAPI().getTokensAPI()
                    .request(numberTokens)
                    .getStatus();

            if (status.equals(TRANSACTION_SUCCESS))
                command.printSuccess();

        } catch (EthereumException e) {
            command.printError("Error during token request");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult();

    }

    @Override
    public CommandResult call() throws CLIException {
        return request();
    }
}
