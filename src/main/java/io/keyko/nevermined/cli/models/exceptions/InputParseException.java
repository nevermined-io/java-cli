package io.keyko.nevermined.cli.models.exceptions;

import io.keyko.nevermined.exceptions.NeverminedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputParseException extends NeverminedException {

    private static final Logger log = LogManager.getLogger(InputParseException.class);

    public InputParseException(String message, Throwable e) {
        super(message, e);
    }

    public InputParseException(String message) {
        super(message);
        log.error(message);
    }
}
