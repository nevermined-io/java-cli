package io.keyko.nevermined.cli.modules.accounts;

import io.keyko.nevermined.cli.AccountsCommand;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.EthereumException;
import io.keyko.nevermined.models.Account;
import io.keyko.nevermined.models.Balance;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "balance",
        description = "Get the balance of an account")
public class AccountsBalance implements Callable {

    @CommandLine.ParentCommand
    AccountsCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0")
    String accountAddress;

    CommandResult balance() throws CLIException {
        Balance balance;
        try {
            command.println("\n@|bold,blue,underline Account Balance:|@");
            command.println("\t@|bold,yellow " + accountAddress + " |@\n");

            balance = command.cli.getNeverminedAPI().getAccountsAPI()
                    .balance(new Account(accountAddress, null));

            command.println("\tPOA Ether: @|bold,yellow " + balance.getEth() + " |@ ETH"
                    + "\n\tTokens: @|bold,yellow " + balance.getOceanTokens() + " |@ Tokens = @|bold,yellow " + balance.getDrops() + " |@ drops");

        } catch (EthereumException e) {
            command.printError("Unable to get account balance");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        }

        return CommandResult.successResult();

    }

    @Override
    public CommandResult call() throws CLIException {
        return balance();
    }
}
