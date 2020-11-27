package io.keyko.nevermined.cli;

import io.keyko.common.exceptions.EncodingException;
import io.keyko.common.helpers.EncodingHelper;
import io.keyko.common.helpers.EthereumHelper;
import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.helpers.CommandLineHelper;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.crypto.*;
import picocli.CommandLine;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static io.keyko.nevermined.cli.TestsBase.TESTS_CONFIG_FOLDER;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilsIT extends TestsBase {

    @Test
    public void utilsInfo() throws CLIException {
        String[] args= {"utils", "info", "https://github.com/robots.txt"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());

    }

    @Test
    public void utilsRandomId() throws CLIException {
        String[] args= {"utils", "random-id"};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());

    }

    @Test
    public void utilsSignAndValidate() throws CLIException {
        final NeverminedCLI cli = new NeverminedCLI(TESTS_CONFIG_FOLDER);

        String message = "Hi dude";

        String[] args= {"utils", "sign", message};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
        String signature = result.getMessage();
        assertFalse(signature.isEmpty());

        String[] validateArgs= {"utils", "validate-signature",
                "--address", cli.getMainAddress(),
                "--message", message,
                "--signature", signature
        };
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), validateArgs);
        assertTrue(result.isSuccess());
    }

    // Code only valid for signatures validation

    @Ignore
    @Test
    public void signAndValidateShort() throws EncodingException, CLIException, IOException, CipherException {
        final NeverminedCLI cli = new NeverminedCLI(TESTS_CONFIG_FOLDER);
        String message = "Hi dude";
        String signatureString = CommandLineHelper.signMessage(message, cli.getCredentials());
        System.out.println("Signature:" + signatureString);
        assertTrue(CommandLineHelper.isValidSignature(message, signatureString, cli.getMainAddress()));
    }

    @Ignore
    @Test
    public void signAndValidate() throws EncodingException, CLIException, IOException, CipherException {
        final NeverminedCLI cli = new NeverminedCLI(TESTS_CONFIG_FOLDER);

        String message = "Hi dude";

        System.out.println("String message to sign and validate: " + message);
        Sign.SignatureData signatureData = EthereumHelper.signMessage(message, cli.getCredentials().getEcKeyPair());

        byte[] hashMessage= EthereumHelper.getEthereumMessageHash(message);
        assertTrue(EthereumHelper.wasSignedByAddress(cli.getCredentials().getAddress(), signatureData, hashMessage));

        String signatureString= EncodingHelper.signatureToString(signatureData);
        System.out.println("Signature:" + signatureString);
        Sign.SignatureData signatureGenerated =
                EncodingHelper.stringToSignature(EthereumHelper.remove0x(signatureString));

        assertTrue(EthereumHelper.wasSignedByAddress(cli.getCredentials().getAddress(), signatureGenerated, hashMessage));
    }

}