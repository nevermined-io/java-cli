package io.keyko.nevermined;

import io.keyko.nevermined.cli.*;
import io.keyko.nevermined.cli.helpers.Version;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.cli.dto.SDKBase;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;


@Command(name = "ncli",
        versionProvider = NeverminedCLI.VersionProvider.class,
        description = "Prints usage help and version help when requested.%n",
        header = {
                "\n~ Welcome to Nevermined CLI ~\n" +
                ""},
        footer = "\nbuilt by Keyko GmbH (https://keyko.io)\n",
        subcommands = {
            ConfigCommand.class,
            AccountsCommand.class,
            TokensCommand.class,
            AssetsCommand.class,
            UtilsCommand.class,
            NetworkCommand.class
        })
public class NeverminedCLI extends SDKBase implements Callable {

    CommandLine.Help.ColorScheme colorScheme = new CommandLine.Help.ColorScheme.Builder()
            .commands    (CommandLine.Help.Ansi.Style.bold, CommandLine.Help.Ansi.Style.underline)    // combine multiple styles
            .options     (CommandLine.Help.Ansi.Style.fg_yellow)                // yellow foreground color
            .parameters  (CommandLine.Help.Ansi.Style.fg_yellow)
            .optionParams(CommandLine.Help.Ansi.Style.italic)
            .build();

    @CommandLine.Option(names = { "-h", "--help" },
            usageHelp = true,
            description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @CommandLine.Option(names = {"-V", "--version"},
            versionHelp = true,
            description = "Display version info")
    boolean versionInfoRequested;

    public NeverminedCLI() throws CLIException {
        super();
    }

    public NeverminedCLI(final String configFolder) throws CLIException {
        super(configFolder);
    }

    public static String getVersion() {
        return "Nevermined CLI v" + Version.VERSION;
    }

    @Override
    public Object call() {
        CommandLine.usage(this, System.out, colorScheme);
        return null;
    }

    public static void main(String[] args) {

        try {
            int exitCode = new CommandLine(new NeverminedCLI()).execute(args);
            System.exit(exitCode);
        } catch (CLIException e) {}
    }


    static class VersionProvider implements CommandLine.IVersionProvider {

        @Override
        public String[] getVersion() throws Exception {
            return new String[] {
                    NeverminedCLI.getVersion()
            };
        }
    }

}
