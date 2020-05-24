package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.asset.OrderResult;
import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.*;

public class AssetsIT extends TestsBase {


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
    public void assetsCreateAndResolve() throws CLIException {

        String[] args= {"assets", "create",
                "--title", "title",
                "--dateCreated", "2012-10-10T17:00:000Z",
                "--author", "aitor",
                "--license", "CC-BY",
                "--contentType", "text/csv",
                "--price", "10",
                "--url", "https://keyko.io/privacy-policy"};

        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
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
        String[] args= {"assets", "import", "src/test/resources/metadata/example-1.json"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
        String did= ((DDO) result.getResult()).id;
        assertTrue(!did.isEmpty());

        String[] argsOrder= {"assets", "order", did, "-s", "3"};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsOrder);
        String saId= ((OrderResult) result.getResult()).getServiceAgreementId();

        assertTrue(result.isSuccess());

        String[] argsConsume= {"assets", "consume", did, "-a", saId, "-s", "3"};
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), argsConsume);
        assertTrue(result.isSuccess());

    }

    @Test
    public void assetsCreateError() throws CLIException {
        String[] args= {"assets", "create",
                "--title", "title",
                "--dateCreated", "2012",
                "--author", "aitor",
                "--license", "CC-BY",
                "--contentType", "text/csv",
                "--price", "10",
                "--url", "https://keyko.io/privacy-policy"};

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