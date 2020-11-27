package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.modules.provenance.*;
import picocli.CommandLine;

@CommandLine.Command(
        name = "provenance",
        subcommands = {
                ProvenanceUsage.class,
                ProvenanceDerivation.class,
                ProvenanceAssociation.class,
                ProvenanceDelegation.class,
                ProvenanceInspect.class,
                ProvenanceHistory.class
        },
        description = "W3C Provenance")
public class ProvenanceCommand extends NeverminedBaseCommand implements Runnable {


    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }

}
