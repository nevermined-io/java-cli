package io.keyko.nevermined.cli.modules.accounts;

import com.google.common.base.Charsets;
import io.keyko.nevermined.api.helper.AccountsHelper;
import io.keyko.nevermined.cli.AccountsCommand;
import io.keyko.nevermined.cli.helpers.Constants;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.apache.commons.io.FileUtils;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "import",
        description = "Import an account from a mnemonic")
public class AccountsImport implements Callable {

    @CommandLine.ParentCommand
    AccountsCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Option(names = { "-m", "--mnemonic" }, required = true, description = "Mnemonic")
    String mnemonic;

    @CommandLine.Option(names = { "-p", "--password" }, arity = "0..1", interactive = true, defaultValue = "", description = "new account password")
    String password;

    @CommandLine.Option(names = { "-d", "--default" }, defaultValue = "false", description = "Make the imported account the default one")
    boolean defaultAccount;

    @CommandLine.Option(names = { "--destination" }, description = "destination path")
    String filePath;

    @CommandLine.Option(names = { "-i", "--index" }, defaultValue = "0", description = "derivation path index (default 0)")
    int index;

    CommandResult newAccount() throws CLIException {
        command.printHeader("Importing account from Mnemonic:");

        try {
            if (null == filePath || filePath.isEmpty())
                filePath= Constants.ACCOUNTS_FOLDER;

            password = "";
            final Credentials credentials = AccountsHelper.loadCredentialsFromMnemonic(mnemonic, password, index);
            final String walletFileName = WalletUtils.generateWalletFile(password, credentials.getEcKeyPair(), new File(filePath), true);
            String address= credentials.getAddress();

            command.println("Account for address " + address + " created at " + walletFileName);

            if (defaultAccount)    {
                command.println("Over-writing config file");
                if (!command.setupNewAccountAsDefault(address, password, walletFileName)) {
                    command.getErr().println("Unable to setup account " + address + " as default in the configuration");
                    return CommandResult.errorResult();
                }
                command.printSuccess();
                command.println("New account " + command.getItem(address) + " imported as default account in the configuration "
                        + command.getItem(Constants.MAIN_CONFIG_FILE));
            }

            return CommandResult.successResult().setMessage(filePath + walletFileName);

        } catch (CipherException | IOException e) {
            command.printError("Unable to import account: " + e.getMessage());
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        }

    }

    @Override
    public CommandResult call() throws CLIException {
        return newAccount();
    }


}
