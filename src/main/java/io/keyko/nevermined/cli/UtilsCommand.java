package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.modules.utils.UtilsInfo;
import io.keyko.nevermined.cli.modules.utils.UtilsRandomId;
import io.keyko.nevermined.cli.modules.utils.UtilsSign;
import io.keyko.nevermined.cli.modules.utils.UtilsValidateSignature;
import picocli.CommandLine;

@CommandLine.Command(
        name = "utils",
        subcommands = {
                UtilsInfo.class,
                UtilsRandomId.class,
                UtilsSign.class,
                UtilsValidateSignature.class
        },
        description = "Utils interface")
public class UtilsCommand extends NeverminedBaseCommand implements Runnable {


    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }
}
