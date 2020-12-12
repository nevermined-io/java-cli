package io.keyko.nevermined.cli.modules.utils;

import io.keyko.nevermined.cli.UtilsCommand;
import io.keyko.nevermined.cli.helpers.CommandLineHelper;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.models.asset.AssetMetadata;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.json.JSONObject;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "random-id",
        description = "It generates a random id that can be used as provenance id or the hash part of a DID")
public class UtilsRandomId implements Callable {

    @CommandLine.ParentCommand
    UtilsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    CommandResult generateRandomId() {
        command.printHeader("Generating Random Identifier:");
        command.printSubHeader(CommandLineHelper.generateRandomIdentifier());
        return CommandResult.successResult();
    }

    @Override
    public Object call() throws CLIException {
        return generateRandomId();
    }
}
