package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.DID;
import org.junit.BeforeClass;
import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.assertTrue;

public class ProvenanceIT extends TestsBase {

    private static String TEST_DID;
    private static final String DELEGATE_ADDRESS = "0xa99d43d86a0758d5632313b8fa3972b6088a21bb";

    @BeforeClass
    public static void setup() throws CLIException {
        logger.info("Requesting for some tokens before running the tests");

        String[] args= {"tokens", "request"};
        CommandResult result = (CommandResult) CommandLine.call(
                new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());

        CommandResult resultPublish = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), TestsBase.PUBLISH_DATASET_PROVENANCE_ARGS);
        assertTrue(resultPublish.isSuccess());
        TEST_DID= ((DDO) resultPublish.getResult()).id;
        assertTrue(!TEST_DID.isEmpty());
    }

    @Test
    public void provenanceUsed() throws CLIException {
        String[] args= {"provenance", "usage", TEST_DID,
                "--agent", "0xa99d43d86a0758d5632313b8fa3972b6088a21bb",
                "--activity", "1234",
                "--attributes", "john doe is using it"
        };
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void provenanceDerivation() throws CLIException, DIDFormatException {
        String[] args= {"provenance", "derivation",
                "--newAsset", DID.builder().getDid(),
                "--derivedFrom", TEST_DID,
                "--agent", "0xa99d43d86a0758d5632313b8fa3972b6088a21bb",
                "--activity", "78jj",
                "--attributes", "john doe is creating a new asset from the previous one"
        };
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void provenanceAssociation() throws CLIException {
        String[] args= {"provenance", "association", TEST_DID,
                "--agent", "0x00a329c0648769A73afAc7F9381E08FB43dBEA72",
                "--activity", "execution",
                "--attributes", "the asset is associated with the execution activity"
        };
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void provenanceDelegation() throws CLIException, DIDFormatException {
        String[] args= {"provenance", "delegation", TEST_DID,
                "--delegatedAgent", "0x00a329c0648769A73afAc7F9381E08FB43dBEA72",
                "--activity", "processing activity",
                "--signature", "0x12345678",
                "--attributes", "alice is able to process the data"
        };
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void provenanceInspect() throws CLIException {
        String[] args= {"provenance", "inspect",
                TEST_DID.replace("did:nv:", "")
        };
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void provenanceHistory() throws CLIException {
        String[] args= {"provenance", "history", TEST_DID };
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void provenanceDelegates() throws CLIException {
        String[] args= {"provenance", "add-delegate", DELEGATE_ADDRESS, TEST_DID};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());

        args= new String[]{"provenance", "is-delegate", DELEGATE_ADDRESS, TEST_DID};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().equals("true"));

        args= new String[]{"provenance", "remove-delegate", DELEGATE_ADDRESS, TEST_DID};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }



}