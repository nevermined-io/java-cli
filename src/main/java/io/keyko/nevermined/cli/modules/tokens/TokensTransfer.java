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
        name = "transfer",
        description = "Transfer Token between accounts")
public class TokensTransfer implements Callable {

    private static final Logger log = LogManager.getLogger(TokensTransfer.class);

    @CommandLine.ParentCommand
    TokensCLI parent;

    @CommandLine.Spec
    public CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(index = "0")
    String receiverAddress;

    @CommandLine.Parameters(index = "1")
    BigInteger drops;

    CommandResult transfer() throws CLIException {
        try {
            parent.spec.commandLine().getOut().println("Transferring " + drops.longValue() +
                    " Token drops from " + parent.cli.getNeverminedAPI().getMainAccount().getAddress() +
                    " to " + receiverAddress);

            parent.cli.progressBar.start();

            String status= parent.cli.getNeverminedAPI().getTokensAPI()
                    .transfer(receiverAddress, drops)
                    .getStatus();

            if (status.equals(TransactionSuccess))
                parent.spec.commandLine().getOut().println("Success!");
        } catch (EthereumException e) {
            log.error(e.getMessage());
            throw new CLIException(e.getMessage());
        } finally {
            parent.cli.progressBar.doStop();
        }
        return CommandResult.successResult();    }

    @Override
    public CommandResult call() throws CLIException {
        return transfer();
    }
}
