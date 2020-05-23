package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.modules.utils.UtilsInfo;
import picocli.CommandLine;

@CommandLine.Command(
        name = "utils",
        subcommands = {
                UtilsInfo.class
        },
        description = "Utils interface")
public class UtilsCommand extends NeverminedBaseCommand implements Runnable {


    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }
}
