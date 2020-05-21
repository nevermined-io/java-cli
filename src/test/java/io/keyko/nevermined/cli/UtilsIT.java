package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.assertTrue;

public class UtilsIT {


    @Test
    public void utilsInfo() throws CLIException {
        String[] args= {"utils", "info", "https://keyko.io/robots.txt"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(), args);
        assertTrue(result.isSuccess());

    }


}