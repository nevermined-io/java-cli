package io.keyko.nevermined.cli;

import io.keyko.common.exceptions.EncodingException;
import io.keyko.common.helpers.EncodingHelper;
import io.keyko.common.helpers.EthereumHelper;
import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.junit.Test;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import picocli.CommandLine;

import java.io.IOException;
import java.util.List;

import static io.keyko.nevermined.cli.TestsBase.TESTS_CONFIG_FOLDER;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilsIT extends TestsBase {

    private static final String TEST_MESSAGE = "247bb0c9b9514238b45b328c2adc83417b5366ef8cd54e7bab9fb41c78a8ebc1";
    private static final String TEST_ADDRESS = "0x00Bd138aBD70e2F00903268F3Db08f2D25677C9e";

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
        String[] args= {"utils", "sign", TEST_MESSAGE};
        CommandResult result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), args);
        assertTrue(result.isSuccess());
        String signature = result.getMessage();
        assertFalse(signature.isEmpty());

        String[] validateArgs= {"utils", "validate-signature",
                "--address", TEST_ADDRESS,
                "--message", TEST_MESSAGE,
                "--signature", signature
        };
        result = (CommandResult) CommandLine.call(new NeverminedCLI(TESTS_CONFIG_FOLDER), validateArgs);
        assertTrue(result.isSuccess());
    }

    @Test
    public void signAndValidate() throws EncodingException, CLIException, IOException, CipherException {
        final NeverminedCLI cli = new NeverminedCLI(TESTS_CONFIG_FOLDER);
        String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";
        String message = "v0G9u7huK4mJb2K1";
        String prefix = PERSONAL_MESSAGE_PREFIX + message.length();
        byte[] hashMessage = Hash.sha3((prefix + message).getBytes());

        Sign.SignatureData signatureGenerated =
                Sign.signPrefixedMessage(hashMessage, cli.getCredentials().getEcKeyPair());

//        String DUMMY_ADDRESS = "0x31b26e43651e9371c88af3d36c14cfd938baf4fd";
//        String signatureString= "0x2c6401216c9031b9a6fb8cbfccab4fcec6c951cdf40e2320108d1856eb532250576865fbcd452bcdc4c57321b619ed7a9cfd38bd973c3e1e0243ac2777fe9d5b1b";
        String DUMMY_ADDRESS = cli.getMainAddress();
        String signatureString= EncodingHelper.signatureToString(signatureGenerated);



        Sign.SignatureData signatureData= EncodingHelper.stringToSignature(EthereumHelper.remove0x(signatureString));
        List<String> addresses = EthereumHelper.recoverAddressFromSignature(signatureData, hashMessage);
        assertTrue(addresses.contains(DUMMY_ADDRESS));

    }

}