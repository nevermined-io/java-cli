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
                "WARNING: This command will not work in environments where the faucet or the dispenser are disabled (i.e: production).")
public class TokensRequest implements Callable {

    @CommandLine.ParentCommand
    TokensCommand command;

    @CommandLine.Mixin
    Logger logger;

    enum TokenOptions { eth, nvm, both }

    @CommandLine.Option(names = { "-t", "--token" }, defaultValue = "both", description = " Network tokens to request. Options: ${COMPLETION-CANDIDATES}"
    + "\n With ETH it's possible to pay network transactions gas"
    + "\n With NVM tokens you can pay for access and computing services.")
    TokenOptions token;

    @CommandLine.Option(names = { "-n", "--nvm" }, defaultValue = "100", description = " Number of Nevermined tokens requested.")
    BigInteger amountNvm;

    CommandResult request() throws CLIException {
        try {
            String accountAddress = command.cli.getNeverminedAPI().getMainAccount().getAddress();

            if (token.equals(TokenOptions.eth) || token.equals(TokenOptions.both))  {

                command.printHeader("Requesting Network ETH for paying transactions gas:");
                final FaucetResponse faucetResponse = command.cli.getNeverminedAPI().getAccountsAPI().requestEthFromFaucet(
                        accountAddress);
                if (faucetResponse.success)
                    command.printSuccess();
                else
                    command.printError(faucetResponse.message);
            }

            if (token.equals(TokenOptions.nvm) || token.equals(TokenOptions.both))  {

                command.printHeader("Requesting Nevermined Tokens:");
                command.println("Requesting " + amountNvm.longValue() +
                        " Token/s for " + accountAddress +
                        " address");

                command.cli.progressBar.start();

                String status= command.cli.getNeverminedAPI().getTokensAPI()
                        .request(amountNvm)
                        .getStatus();

                if (status.equals(TRANSACTION_SUCCESS))
                    command.printSuccess();
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
