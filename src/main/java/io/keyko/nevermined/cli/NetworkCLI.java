package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.modules.contract.ContractDescribe;
import io.keyko.nevermined.cli.modules.contract.ContractList;
import picocli.CommandLine;

@CommandLine.Command(
        name = "contract",
        subcommands = {
                ContractList.class,
                ContractDescribe.class
        },
        description = "Nevermined Smart Contracts interface")
public class NetworkCLI extends NeverminedCommand implements Runnable {

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }


}
