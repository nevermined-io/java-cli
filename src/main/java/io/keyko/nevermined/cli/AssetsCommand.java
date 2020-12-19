package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.modules.assets.*;
import picocli.CommandLine;

@CommandLine.Command(
        name = "assets",
        subcommands = {
                AssetsPublishDataset.class,
                AssetsPublishAlgorithm.class,
                AssetsPublishWorkflow.class,
                AssetsImport.class,
                AssetsResolve.class,
                AssetsSearch.class,
                AssetsOrder.class,
                AssetsGet.class,
                AssetsDownloadMyAsset.class,
                AssetsExec.class
        },
        description = "Assets handler")
public class AssetsCommand extends NeverminedBaseCommand implements Runnable {


    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }




}
