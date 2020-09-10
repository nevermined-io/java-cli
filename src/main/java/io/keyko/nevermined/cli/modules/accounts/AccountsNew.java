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

    @CommandLine.Option(names = { "-p", "--password" }, arity = "0..1", interactive = true, required = true, description = "new account password, if it's not given will be auto-generated")
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
                if (!setupNewAccountAsDefault(address, password, filePath + File.separator + accountPath)) {
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

    private boolean setupNewAccountAsDefault(String address, String password, String filePath)  {

        try {
            String defaultConfigContent= FileUtils.readFileToString(new File(Constants.MAIN_CONFIG_FILE), Charsets.UTF_8);
            String newConfigContent= defaultConfigContent
                    .replaceAll("account.main.address=\"(.*?)\"", "account.main.address=\""+ address +"\"")
                    .replaceAll("account.main.password=\"(.*?)\"", "account.main.password=\""+ password +"\"")
                    .replaceAll("account.main.credentialsFile=\"(.*?)\"", "account.main.credentialsFile=\""+ filePath +"\"");

            FileUtils.writeStringToFile(new File(Constants.MAIN_CONFIG_FILE), newConfigContent, Charsets.UTF_8);

            return true;
        } catch (IOException ex)    {
            command.printError("Unable to setup account as default: " + ex.getMessage());
        }

        return false;
    }
}
