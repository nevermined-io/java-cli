package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import picocli.CommandLine;
import io.keyko.nevermined.cli.modules.utils.UtilsInfo;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "utils",
        subcommands = {
                UtilsInfo.class
        },
        description = "Utils interface")
public class UtilsCLI extends NeverminedCommand implements Callable {

    @CommandLine.ParentCommand
    public NeverminedCLI cli;

    @CommandLine.Spec
    public CommandLine.Model.CommandSpec spec;


    @Override
    public Object call() {
        spec.commandLine().usage(System.out);
        return null;
    }


}
