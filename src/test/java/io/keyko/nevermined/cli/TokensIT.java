package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.BeforeClass;
import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.assertTrue;

public class TokensIT extends TestsBase {


    @BeforeClass
    public static void setup()  {
        logger.debug("Main account address: " + config.getString("account.main.address"));
        logger.debug("Main test address: " + config.getString("account.test.address"));

    }

    @Test
    public void tokenAndtransferRequest() throws CLIException {
        String[] args= {"tokens", "request", "--tokens", "1"};
        CommandResult result = (CommandResult) CommandLine.call(
                new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());

        String[] argsTransfer= {"tokens", "transfer", config.getString("account.test.address"), "1"};
        CommandResult resultTransfer = (CommandResult) CommandLine.call(
                new NeverminedCLI(TESTS_CONFIG_FOLDER), argsTransfer);
        assertTrue(resultTransfer.isSuccess());
    }


}
