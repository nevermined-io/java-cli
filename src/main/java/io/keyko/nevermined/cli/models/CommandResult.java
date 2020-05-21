package io.keyko.nevermined.cli.models;

import io.keyko.nevermined.models.AbstractModel;

public class CommandResult {

    public static int SUCCESS= 0;

    int status;

    String message;

    AbstractModel result;

    public CommandResult(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static CommandResult successResult() {
        return new CommandResult(SUCCESS, "Success");
    }


    public static CommandResult errorResult() {
        return new CommandResult(1, "Error");
    }

    public static CommandResult errorResult(String message) {
        return new CommandResult(1, message);
    }

    public boolean isSuccess()  {
        return status == SUCCESS;
    }

    public int getStatus() {
        return status;
    }

    public CommandResult setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CommandResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public AbstractModel getResult() {
        return result;
    }

    public CommandResult setResult(AbstractModel result) {
        this.result = result;
        return this;
    }

}
