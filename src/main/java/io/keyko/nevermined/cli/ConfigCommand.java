package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.modules.config.ConfigClean;
import io.keyko.nevermined.cli.modules.config.ConfigShow;
import picocli.CommandLine;

@CommandLine.Command(
        name = "config",
        subcommands = {ConfigClean.class, ConfigShow.class},
        description = "Reading and setting application config")
public class ConfigCommand extends NeverminedBaseCommand implements Runnable {

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }

}
