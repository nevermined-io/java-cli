package io.keyko.nevermined.cli;

import com.google.common.io.Files;
import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.Test;
import picocli.CommandLine;

import java.io.File;

import static io.keyko.nevermined.cli.TestsBase.TESTS_CONFIG_FOLDER;
import static org.junit.Assert.assertTrue;
public class ConfigIT {

    @Test
    public void showConfig() throws CLIException {
        String[] args= {"config", "show"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }


    @Test
    public void createNetworkFiles() throws CLIException {
        File tempDir = Files.createTempDir();

        String[] args= {"config", "show"};
        CommandResult result = (CommandResult) CommandLine.call(
                new NeverminedCLI(tempDir.getAbsolutePath()), args);
        assertTrue(result.isSuccess());
    }

}
