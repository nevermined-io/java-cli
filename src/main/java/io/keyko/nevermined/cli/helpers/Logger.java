package io.keyko.nevermined.cli.helpers;

import picocli.CommandLine;

public class Logger {

    @CommandLine.Option(names = {"-v", "--verbose"},
            description = "Increase verbosity. Specify multiple times to increase (-vvv).")
    boolean[] verbosity = new boolean[0];

    public void info(String pattern, Object... params) {
        log(0, pattern, params);
    }

    public void debug(String pattern, Object... params) {
        log(1, pattern, params);
    }

    public void trace(String pattern, Object... params) {
        log(2, pattern, params);
    }

    private void log(int level, String pattern, Object... params) {
        if (verbosity.length > level) {
            System.err.printf(pattern, params);
        }
    }
}
