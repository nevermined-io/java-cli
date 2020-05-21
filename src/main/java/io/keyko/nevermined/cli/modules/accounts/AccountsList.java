package io.keyko.nevermined.cli.modules.accounts;

import io.keyko.nevermined.cli.AccountsCLI;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.EthereumException;
import io.keyko.nevermined.models.Account;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "list",
        description = "List all the existing accounts")
public class AccountsList implements Callable {

    @CommandLine.ParentCommand
    AccountsCLI command;

    @CommandLine.Mixin
    Logger logger;


    CommandResult list() {
        command.println("\n@|bold,blue,underline Listing Accounts:|@\n");

        List<Account> accounts;
        try {
            accounts= command.cli.getNeverminedAPI().getAccountsAPI().list();
        } catch (EthereumException | CLIException e) {
            command.printError("Unable to retrieve accounts info");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        }

        for (Account account: accounts) {
            command.println("\t@|bold,yellow " + account.getAddress() + ":|@ ");
        }
        return CommandResult.successResult();
    }

    @Override
    public CommandResult call() {
        return list();
    }
}
