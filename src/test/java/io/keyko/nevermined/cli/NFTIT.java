package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.models.DDO;
import org.junit.BeforeClass;
import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NFTIT extends TestsBase {

    private static String did;

    @BeforeClass
    public static void setup() throws CLIException {
        logger.info("Requesting for some tokens before running the tests");

        String[] args= {"tokens", "request"};
        CommandResult result = (CommandResult) CommandLine.call(
                new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());

        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), PUBLISH_DATASET_ARGS);
        assertTrue(result.isSuccess());
        did= ((DDO) result.getResult()).id;
        assertTrue(!did.isEmpty());
    }

    @Test
    public void mintAndBurn() throws CLIException {
        String someoneElse = "0x068ed00cf0441e4829d9784fcbe7b9e26d4bd8d0";

        String[] args= {"nft", "mint", did, "10"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());

        String[] argsBurn= {"nft", "burn", did, "2"};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsBurn);
        assertTrue(result.isSuccess());

        String[] argsTransfer= {"nft", "transfer", did, someoneElse, "3"};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsTransfer);
        assertTrue(result.isSuccess());

        String[] argsBalance= {"nft", "balance", did, TEST_ADDRESS};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsBalance);
        assertTrue(result.isSuccess());
        assertEquals("5", result.getMessage());

        String[] argsBalanceSomeoneElse= {"nft", "balance", did, someoneElse};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsBalanceSomeoneElse);
        assertTrue(result.isSuccess());
        assertEquals("3", result.getMessage());
    }


}