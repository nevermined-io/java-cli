package io.keyko.nevermined.cli.modules.utils;

import io.keyko.nevermined.cli.NeverminedBaseCommand;
import io.keyko.nevermined.cli.UtilsCommand;
import io.keyko.nevermined.cli.helpers.CommandLineHelper;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "validate-signature",
        description = "It validates a ECDSA signature")
public class UtilsValidateSignature implements Callable {

    @CommandLine.ParentCommand
    UtilsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Option(names = { "-a", "--address" }, required = true,
            description = "The public ethereum address of the signer")
    String address;

    @CommandLine.Option(names = { "-m", "--message" }, required = true,
            description = "The original message")
    String message;

    @CommandLine.Option(names = { "-s", "--signature" }, required = true,
            description = "The signature to validate")
    String signature;

    CommandResult validateSignature() {
        command.printHeader("Validate Signature: ");

        if (CommandLineHelper.isValidSignature(message, signature, address))    {
            command.print(NeverminedBaseCommand.SUCCESS_INDICATOR + "The signature is valid");
            return CommandResult.successResult();
        }   else    {
            command.print(NeverminedBaseCommand.ERROR_INDICATOR + "The signature is not valid");
            return CommandResult.errorResult();
        }
    }

    @Override
    public Object call() throws CLIException {
        return validateSignature();
    }
}
