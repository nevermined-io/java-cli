package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.assertTrue;

public class AccountsIT {


    @Test
    public void newAccount() throws CLIException {
        String[] args= {"accounts", "new", "-p", "1234", "-d", "/tmp"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void newAccountDefault() throws CLIException {
        String[] args= {"accounts", "new", "-p", "1234", "-m"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(), args);
        assertTrue(result.isSuccess());

    }

    @Test
    public void listAccounts() throws CLIException {
        String[] args= {"accounts", "list"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void balanceAccount() throws CLIException {
        String[] args= {"accounts", "balance", "0x00Bd138aBD70e2F00903268F3Db08f2D25677C9e"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(), args);
        assertTrue(result.isSuccess());
    }

}