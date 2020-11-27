package io.keyko.nevermined.cli.modules.utils;

import io.keyko.nevermined.cli.UtilsCommand;
import io.keyko.nevermined.cli.helpers.CommandLineHelper;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "sign",
        description = "It sign a message using the ECDSA credentials")
public class UtilsSign implements Callable {

    @CommandLine.ParentCommand
    UtilsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String message;

    CommandResult sign() {
        command.printHeader("Signing the message: " + message);

        try {
            String signature = CommandLineHelper.signMessage(
                    message,
                    command.cli.getCredentials()
            );
            command.printSubHeader("Address used for signing: " + command.cli.getMainAddress());
            command.printSubHeader("Signature: " + signature);
            return CommandResult.successResult().setMessage(signature);
        } catch (IOException | CipherException e) {
            command.printError("Error signing the message: " + e.getMessage());
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        }
    }

    @Override
    public Object call() throws CLIException {
        return sign();
    }
}
