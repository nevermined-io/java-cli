package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.assertTrue;
public class ConfigIT {

    @Test
    public void showConfig() throws CLIException {
        String[] args= {"config", "show"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(), args);
        assertTrue(result.isSuccess());
    }

}
