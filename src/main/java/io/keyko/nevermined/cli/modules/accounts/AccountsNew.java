package io.keyko.nevermined.cli.modules.accounts;

import io.keyko.nevermined.api.helper.AccountsHelper;
import io.keyko.nevermined.cli.AccountsCommand;
import io.keyko.nevermined.cli.helpers.Constants;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.web3j.crypto.CipherException;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "new",
        description = "Create a new account")
public class AccountsNew implements Callable {

    @CommandLine.ParentCommand
    AccountsCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Option(names = { "-p", "--password" }, arity = "0..1", interactive = true, required = true, description = "new account password")
    String password;

    @CommandLine.Option(names = { "-d", "--destination" }, description = "destination path")
    String filePath;

    @CommandLine.Option(names = { "-m", "--main" }, defaultValue = "false", description = "configure the new account as main account in the config file")
    boolean makeMainAccount;

    CommandResult newAccount() throws CLIException {
        command.printHeader("Creating new account:");

        try {
            if (null == filePath || filePath.isEmpty())
                filePath= Constants.ACCOUNTS_FOLDER;

            String accountPath= AccountsHelper.createAccount(password, filePath);
            String address= AccountsHelper.getAddressFromFilePath(accountPath);

            command.println("Account for address " + address + " created at " + filePath + File.separator + accountPath);

            if (makeMainAccount)    {
                command.println("Over-writing config file");
                if (!command.setupNewAccountAsDefault(address, password, filePath + File.separator + accountPath)) {
                    command.getErr().println("Unable to setup account " + address + " as default in the configuration");
                    return CommandResult.errorResult();
                }
                command.printSuccess();
                command.println("New account " + command.getItem(address) + " added as default account in the configuration "
                        + command.getItem(Constants.MAIN_CONFIG_FILE));
            }

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CipherException | IOException e) {
            command.printError("Unable to create account: " + e.getMessage());
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        }

        return CommandResult.successResult();
    }

    @Override
    public CommandResult call() throws CLIException {
        return newAccount();
    }

}
