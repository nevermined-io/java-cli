package io.keyko.nevermined.cli.modules.accounts;

import io.keyko.nevermined.cli.AccountsCLI;
import io.keyko.nevermined.cli.helpers.AccountsHelper;
import io.keyko.nevermined.cli.helpers.Constants;
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
    AccountsCLI command;

    @CommandLine.Option(names = { "-p", "--password" }, description = "new account password, if it's not given will be auto-generated")
    String password;

    @CommandLine.Option(names = { "-d", "--destination" }, description = "destination path")
    String filePath;

    @CommandLine.Option(names = { "-m", "--main" }, defaultValue = "false", description = "configure the new account as main account in the config file")
    boolean makeMainAccount;

    CommandResult newAccount() throws CLIException {
        command.println("Creating new account:");

        try {
            if (null == filePath || filePath.isEmpty())
                filePath= Constants.accountsFolder;

            String accountPath= AccountsHelper.createAccount(password, filePath);
            String address= AccountsHelper.getAddressFromFilePath(accountPath);

            command.println("Account for address " + address + " created at " + filePath + File.separator + accountPath);

            if (makeMainAccount)    {
                command.println("Over-writing config file");
                if (!setupNewAccountAsDefault(address, password, filePath + File.separator + accountPath)) {
                    command.getErr().println("Unable to setup account " + address + " as default in the configuration");
                    return CommandResult.errorResult();
                }
                command.println("New account " + address + " added as default account in the configuration " + Constants.mainConfigFile);
            }

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CipherException | IOException e) {
            command.printError("Unable to create account: " + e.getMessage());
            throw new CLIException("Unable to create account: " + e.getMessage());
        }

        return CommandResult.successResult();
    }

    @Override
    public CommandResult call() throws CLIException {
        return newAccount();
    }

    private boolean setupNewAccountAsDefault(String address, String password, String filePath)  {

        try {
            String defaultConfigContent= FileUtils.readFileToString(new File(Constants.mainConfigFile), Charsets.UTF_8);
            String newConfigContent= defaultConfigContent
                    .replaceAll("account.main.address=\"(.*?)\"", "account.main.address=\""+ address +"\"")
                    .replaceAll("account.main.password=\"(.*?)\"", "account.main.password=\""+ password +"\"")
                    .replaceAll("account.main.credentialsFile=\"(.*?)\"", "account.main.credentialsFile=\""+ filePath +"\"");

            FileUtils.writeStringToFile(new File(Constants.mainConfigFile), newConfigContent, Charsets.UTF_8);

            return true;
        } catch (IOException ex)    {
            command.printError("Unable to setup account as default: " + ex.getMessage());
        }

        return false;
    }
}
