package io.keyko.nevermined.cli.modules.tokens;

import io.keyko.nevermined.cli.TokensCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.EthereumException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.math.BigInteger;
import java.util.concurrent.Callable;

import static io.keyko.nevermined.cli.helpers.Constants.TransactionSuccess;

@CommandLine.Command(
        name = "request",
        description = "Request some Tokens")
public class TokensRequest implements Callable {

    private static final Logger log = LogManager.getLogger(TokensRequest.class);

    @CommandLine.ParentCommand
    TokensCLI parent;

    @CommandLine.Spec
    public CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(index = "0")
    BigInteger numberTokens;

    CommandResult request() throws CLIException {
        try {
            parent.spec.commandLine().getOut().println("Requesting " + numberTokens.longValue() +
                    " Token/s for " + parent.cli.getNeverminedAPI().getMainAccount().getAddress() +
                    " address");

            parent.cli.progressBar.start();

            String status= parent.cli.getNeverminedAPI().getTokensAPI()
                    .request(numberTokens)
                    .getStatus();

            if (status.equals(TransactionSuccess))
                parent.spec.commandLine().getOut().println("\nSuccess!\n");

        } catch (EthereumException e) {
            log.error(e.getMessage());
            throw new CLIException(e.getMessage());
        } finally {
            parent.cli.progressBar.doStop();
        }
        return CommandResult.successResult();

    }

    @Override
    public CommandResult call() throws CLIException {
        return request();
    }
}
