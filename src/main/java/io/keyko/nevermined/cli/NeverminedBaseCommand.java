package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import picocli.CommandLine;

import java.io.PrintWriter;

public class NeverminedBaseCommand {

    public static final String ERROR_PREFIX = "@|bold,red Error:|@\n";
    public static final String SUCCESS_MESSAGE = "@|bold,green Success [✔] |@";
    public static final String ERROR_MESSAGE = "@|bold,red Error [✘] |@";


    @CommandLine.ParentCommand
    public NeverminedCLI cli;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public void println()   {
        println("");
    }

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

    public void printSuccess()  {
        println(SUCCESS_MESSAGE);
    }

    public PrintWriter getOut() {
        return spec.commandLine().getOut();
    }

    public PrintWriter getErr() {
        return spec.commandLine().getErr();
    }

}
