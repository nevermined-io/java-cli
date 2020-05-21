package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.modules.accounts.AccountsBalance;
import io.keyko.nevermined.cli.modules.accounts.AccountsList;
import io.keyko.nevermined.cli.modules.accounts.AccountsNew;
import picocli.CommandLine;

@CommandLine.Command(
        name = "accounts",
        subcommands = {AccountsNew.class, AccountsList.class, AccountsBalance.class},
        description = "Allowing to interact with the accounts.")
public class AccountsCLI extends NeverminedCommand implements Runnable {

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }
}
