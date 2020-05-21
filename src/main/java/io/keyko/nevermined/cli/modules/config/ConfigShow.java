package io.keyko.nevermined.cli.modules.config;

import io.keyko.nevermined.cli.ConfigCLI;
import io.keyko.nevermined.cli.models.CommandResult;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "show",
        description = "Show all the config information used")
public class ConfigShow implements Callable {

    @CommandLine.ParentCommand
    ConfigCLI parent;

    CommandResult showConfig() {
        parent.println("\n@|bold,blue,underline Main Config:|@\n");

        parent.cli.getMainConfig().entrySet().forEach(e ->{
            parent.println("\t@|bold,yellow " + e.getKey() + ":|@ " + e.getValue().render());
        });

        parent.println("\n@|bold,blue,underline Network Config:|@\n");
        parent.cli.getNetworkConfig().entrySet().forEach(e -> {
            parent.println("\t@|bold,yellow " + e.getKey() + ":|@ " + e.getValue().render());
        });
        parent.println("");
        return CommandResult.successResult();
    }

    @Override
    public CommandResult call() {
        return showConfig();
    }

}
