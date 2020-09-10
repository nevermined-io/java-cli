package io.keyko.nevermined.cli;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.Ignore;
import org.junit.Test;
import picocli.CommandLine;

import static io.keyko.nevermined.cli.TestsBase.TESTS_CONFIG_FOLDER;
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
        String[] args= {"accounts", "balance", config.getString("account.main.address")};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);

        assertTrue(result.isSuccess());
    }

}