package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import picocli.CommandLine;

import java.io.PrintWriter;

public class NeverminedCommand {

    private static final String ERROR_PREFIX = "@|bold,red Error:|@\n";

    @CommandLine.ParentCommand
    public NeverminedCLI cli;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public void println(String line)   {
        getOut().println(
                CommandLine.Help.Ansi.AUTO.string(line)
        );
    }

    public void printError(String line)   {
        getErr().println(
                CommandLine.Help.Ansi.AUTO.string(ERROR_PREFIX + line)
        );
    }


    public PrintWriter getOut() {
        return spec.commandLine().getOut();
    }

    public PrintWriter getErr() {
        return spec.commandLine().getErr();
    }

}
