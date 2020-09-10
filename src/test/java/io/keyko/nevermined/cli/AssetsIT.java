package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.DID;
import io.keyko.nevermined.models.asset.OrderResult;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.*;

public class AssetsIT extends TestsBase {

    @BeforeClass
    public static void setup() throws CLIException {
        logger.info("Requesting for some tokens before running the tests");

        String[] args= {"tokens", "request", "--tokens", "10"};
        CommandResult result = (CommandResult) CommandLine.call(
                new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void assetsImport() throws CLIException {
        String[] args= {"assets", "import", "src/test/resources/metadata/example-1.json"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
        assertTrue(!((DDO) result.getResult()).id.isEmpty());

    }

    @Test
    public void assetsImportError() throws CLIException {
        String[] args= {"assets", "import", "dksal/xxxx.json"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertFalse(result.isSuccess());
    }

    @Test
    public void assetsSearch() throws CLIException {
        String[] args= {"assets", "search", "weather"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
    }

    @Test
    public void assetsPublishAndResolveDataset() throws CLIException {

        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), PUBLISH_DATASET_ARGS);
        assertTrue(result.isSuccess());
        String did= ((DDO) result.getResult()).id;
        assertTrue(!did.isEmpty());

        String[] argsResolve= {"assets", "resolve", did};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsResolve);
        assertTrue(result.isSuccess());
        assertEquals(did, ((DDO) result.getResult()).id);

    }

    @Test
    public void assetsPublishAndResolveAlgorithm() throws CLIException {

        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), PUBLISH_ALGORITHM_ARGS);
        assertTrue(result.isSuccess());
        String did= ((DDO) result.getResult()).id;
        assertTrue(!did.isEmpty());

        String[] argsResolve= {"assets", "resolve", did};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsResolve);
        assertTrue(result.isSuccess());
        assertEquals(did, ((DDO) result.getResult()).id);

    }


    @Test
    public void assetsPublishAndResolveWorkflow() throws CLIException, DIDFormatException {

        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), PUBLISH_WORKFLOW_ARGS);
        assertTrue(result.isSuccess());
        String did= ((DDO) result.getResult()).id;
        assertTrue(!did.isEmpty());

        String[] argsResolve= {"assets", "resolve", did};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsResolve);
        assertTrue(result.isSuccess());
        assertEquals(did, ((DDO) result.getResult()).id);

    }

    @Test
    public void assetsPublishAndResolveDatasetCompute() throws CLIException {

        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), PUBLISH_COMPUTE_ARGS);
        assertTrue(result.isSuccess());
        String did= ((DDO) result.getResult()).id;
        assertTrue(!did.isEmpty());

        String[] argsResolve= {"assets", "resolve", did};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsResolve);
        assertTrue(result.isSuccess());
        assertEquals(did, ((DDO) result.getResult()).id);
    }

    @Test
    public void assetsImportAndAccess() throws CLIException {
        String[] args= {"assets", "import", "src/test/resources/metadata/example-1.json", "--service", "access"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
        String did= ((DDO) result.getResult()).id;
        assertTrue(!did.isEmpty());

        String[] argsOrderAndGet= {"assets", "get", did, };
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsOrderAndGet);
        assertTrue(result.isSuccess());

        String[] argsOrder= {"assets", "order", did, "-s", "3"};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsOrder);
        String saId= ((OrderResult) result.getResult()).getServiceAgreementId();

        assertTrue(result.isSuccess());

        String[] argsGet= {"assets", "get", did, "-s", saId, "-i", "3"};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsGet);
        assertTrue(result.isSuccess());

    }


    // TODO: Have all the compute technical components automated
    @Ignore
    @Test
    public void computeE2E() throws CLIException {

        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), PUBLISH_COMPUTE_ARGS);
        assertTrue(result.isSuccess());
        String didCompute= ((DDO) result.getResult()).id;
        assertTrue(!didCompute.isEmpty());

        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), PUBLISH_ALGORITHM_ARGS);
        assertTrue(result.isSuccess());
        String didAlgorithm= ((DDO) result.getResult()).id;
        assertTrue(!didAlgorithm.isEmpty());

        String [] workflowArgs = new String[]{"assets", "publish-workflow",
                "--title", "word count workflow",
                "--dateCreated", "2012-11-11T17:00:000Z",
                "--author", "aitor",
                "--container", "python:3.8-alpine",
                "--inputs", didCompute,
                "--transformation", didAlgorithm
        };
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), workflowArgs);
        assertTrue(result.isSuccess());
        String didWorkflow= ((DDO) result.getResult()).id;
        assertTrue(!didWorkflow.isEmpty());


        String [] execArgs = new String[]{"assets", "exec",
                didCompute,
                "--workflow", didWorkflow
        };
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), execArgs);
        assertTrue(result.isSuccess());
    }


    @Test
    public void assetsCreateError() throws CLIException {
        String[] args= {"assets", "publish-dataset",
                "--title", "title",
                "--dateCreated", "2012",
                "--author", "aitor",
                "--license", "CC-BY",
                "--contentType", "text/csv",
                "--price", "10",
                "--urls", "https://keyko.io/privacy-policy"};

        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertFalse(result.isSuccess());
    }

    @Test
    public void assetsResolveError() throws CLIException {
        String[] args= {"assets", "resolve", "did:op:1234"};

        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertFalse(result.isSuccess());
    }

}