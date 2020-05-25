package io.keyko.nevermined.cli.modules.config;

import io.keyko.nevermined.cli.ConfigCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "show",
        description = "Show all the config information used")
public class ConfigShow implements Callable {

    @CommandLine.ParentCommand
    ConfigCommand command;

    CommandResult showConfig() {
        command.printHeader("Main Config:");

        command.cli.getMainConfig().entrySet().forEach(e ->{
            command.printKeyValue(e.getKey(), e.getValue().render());
        });

        command.printHeader("Network Config:");
        command.cli.getNetworkConfig().entrySet().forEach(e -> {
            command.printKeyValue(e.getKey(), e.getValue().render());
        });
        command.println();
        return CommandResult.successResult();
    }

    @Override
    public CommandResult call() {
        return showConfig();
    }

}
