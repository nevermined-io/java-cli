package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.Test;
import picocli.CommandLine;

import static io.keyko.nevermined.cli.TestsBase.TESTS_CONFIG_FOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NetworkIT {


    @Test
    public void contractList() throws CLIException {
        String[] args= {"contract", "list"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void contractDescribe() throws CLIException {
        String[] args= {"contract", "describe", "DIDRegistry"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

}