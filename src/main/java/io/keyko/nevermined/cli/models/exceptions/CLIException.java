package io.keyko.nevermined.cli.models.exceptions;

import io.keyko.nevermined.exceptions.NeverminedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CLIException extends NeverminedException {

    private static final Logger log = LogManager.getLogger(CLIException.class);

    public CLIException(String message, Throwable e) {
        super(message, e);
    }

    public CLIException(String message) {
        super(message);
        log.error(message);
    }
}
