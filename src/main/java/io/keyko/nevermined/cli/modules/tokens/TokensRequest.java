package io.keyko.nevermined.cli.modules.tokens;

import io.keyko.nevermined.cli.TokensCommand;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.EthereumException;
import io.keyko.nevermined.exceptions.ServiceException;
import io.keyko.nevermined.models.faucet.FaucetResponse;
import picocli.CommandLine;

import java.math.BigInteger;
import java.util.concurrent.Callable;

import static io.keyko.nevermined.cli.helpers.Constants.TRANSACTION_SUCCESS;

@CommandLine.Command(
        name = "request",
        description = "Request some Tokens and ETH for paying transactions gas.\n" +
                "WARNING: This command will environments where the faucet or the dispenser are disabled (i.e: production).")
public class TokensRequest implements Callable {

    @CommandLine.ParentCommand
    TokensCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Option(names = { "-e", "--eth" }, defaultValue = "false", description = "ETH for paying transactions gas")
    boolean eth;

    @CommandLine.Option(names = { "-t", "--tokens" }, defaultValue = "0", description = "Nevermined tokens")
    BigInteger numberTokens;


    CommandResult request() throws CLIException {
        try {
            String accountAddress = command.cli.getNeverminedAPI().getMainAccount().getAddress();

            if (numberTokens.compareTo(BigInteger.ZERO) > 0)    {
                command.printHeader("Requesting Nevermined Tokens:");
                command.println("Requesting " + numberTokens.longValue() +
                        " Token/s for " + accountAddress +
                        " address");

                command.cli.progressBar.start();

                String status= command.cli.getNeverminedAPI().getTokensAPI()
                        .request(numberTokens)
                        .getStatus();

                if (status.equals(TRANSACTION_SUCCESS))
                    command.printSuccess();
            }

            if (eth)    {
                command.printHeader("Requesting Network ETH for paying transactions gas:");
                final FaucetResponse faucetResponse = command.cli.getNeverminedAPI().getAccountsAPI().requestEthFromFaucet(
                        accountAddress);
                if (faucetResponse.success)
                    command.printSuccess();
                else
                    command.printError(faucetResponse.message);
            }

        } catch (EthereumException | ServiceException e) {
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
