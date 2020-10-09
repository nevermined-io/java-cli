package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import picocli.CommandLine;

import java.io.PrintWriter;

public class NeverminedBaseCommand {

    public static final String COLOR_PREFIX = "@|";
    public static final String COLOR_SUFFIX = "|@ ";
    public static final String ERROR_PREFIX = COLOR_PREFIX + "bold,red Error:" + COLOR_SUFFIX + "\n";
    public static final String SUCCESS_MESSAGE = COLOR_PREFIX + "bold,green Success [✔] " + COLOR_SUFFIX;
    public static final String ERROR_MESSAGE = COLOR_PREFIX + "bold,red Error [✘] " + COLOR_SUFFIX;
    public static final String HEADER_PREFIX = COLOR_PREFIX + "bold,blue ";
    public static final String SUBHEADER_PREFIX = COLOR_PREFIX + "bold,yellow ";
    public static final String ITEM_PREFIX = COLOR_PREFIX + "yellow ";


    @CommandLine.ParentCommand
    public NeverminedCLI cli;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public void print(String line)   {
        if (!line.contains(COLOR_PREFIX))
            getOut().print(line);
        else
            getOut().print(
                CommandLine.Help.Ansi.AUTO.string(line)
            );
        getOut().flush();
    }

    public void println(String line) {
        print(line + "\n");
    }

    public void println() {
        print("\n");
    }

    public void printHeader(String header)  {
        println("\n" + HEADER_PREFIX + header + COLOR_SUFFIX + "\n");
    }

    public void printSubHeader(String text)  {
        println("\t" + SUBHEADER_PREFIX + text + COLOR_SUFFIX + "\n");
    }

    public void printItem(String text)  {
        println(getItem(text));
    }

    public void printKeyValue(String key, String value)  {
        println(getItem(key) + ":" + value);
    }

    public String getItem(String text)  {
        return ITEM_PREFIX + text + COLOR_SUFFIX;
    }

    public void printError(String line)   {
        getErr().println(
                CommandLine.Help.Ansi.AUTO.string(ERROR_PREFIX + line)
        );
    }

    public void printSuccess()  {
        println(SUCCESS_MESSAGE);
    }

    public void printStatusRunning(String text) {
        print(COLOR_PREFIX + "yellow  " + text + COLOR_SUFFIX);
    }

    public void printStatusSucceeded(String text) {
        print(COLOR_PREFIX + "green  " + text + COLOR_SUFFIX);
    }

    public void printStatusFailed(String text) {
        print(COLOR_PREFIX + "red  " + text + COLOR_SUFFIX);
    }

    public PrintWriter getOut() {
        return spec.commandLine().getOut();
    }

    public PrintWriter getErr() {
        return spec.commandLine().getErr();
    }

}
