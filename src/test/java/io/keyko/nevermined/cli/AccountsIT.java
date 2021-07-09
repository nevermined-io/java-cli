package io.keyko.nevermined.cli;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

import static io.keyko.nevermined.cli.TestsBase.TESTS_CONFIG_FOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountsIT {

    Config config = ConfigFactory.load();


    @Test
    public void newAccount() throws CLIException {
        String[] args= {"accounts", "new", "-p", "1234", "-d", "/tmp"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    @Ignore
    public void newAccountDefault() throws CLIException {
        String[] args= {"accounts", "new", "-p", "1234", "-m"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void listAccounts() throws CLIException {
        String[] args= {"accounts", "list"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void balanceAccount() throws CLIException {
        String[] args= {"accounts", "balance", "-a", config.getString("account.main.address")};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);

        assertTrue(result.isSuccess());

        String [] argsNoAccount= {"accounts", "balance"};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsNoAccount);

        assertTrue(result.isSuccess());
    }

    @Test
    public void importAccount() throws CLIException, IOException, CipherException {
        String[] args= {"accounts", "import",
                "--mnemonic", "taxi music thumb unique chat sand crew more leg another off lamp",
                "--destination", "/tmp"
        };
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
        final Credentials credentials = WalletUtils.loadCredentials("", new File(result.getMessage()));
        assertEquals(
                "0xe2DD09d719Da89e5a3D0F2549c7E24566e947260".toLowerCase(), credentials.getAddress().toLowerCase());
    }

}