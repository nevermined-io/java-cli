package io.keyko.nevermined.cli.modules.config;

import io.keyko.nevermined.cli.ConfigCommand;
import io.keyko.nevermined.cli.helpers.Constants;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

@CommandLine.Command(
        name = "clean",
        description = "Remove any existing config")
public class ConfigClean implements Runnable {

    @CommandLine.ParentCommand
    ConfigCommand command;

    @CommandLine.Mixin
    Logger logger;

    CommandResult clean() {
        command.printHeader("Cleaning existing config");

        try {
            FileUtils.deleteDirectory(new File(Constants.CONFIG_FOLDER));
        } catch (IOException e) {
            command.printError("Unable to delete config folder " + Constants.CONFIG_FOLDER);
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        }
        command.printSuccess();
        return CommandResult.successResult();
    }

    @Override
    public void run() {
        clean();
    }
}
