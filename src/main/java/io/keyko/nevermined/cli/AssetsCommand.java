package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.helpers.Constants;
import io.keyko.nevermined.cli.modules.assets.*;
import io.keyko.nevermined.models.service.ProviderConfig;
import picocli.CommandLine;

@CommandLine.Command(
        name = "assets",
        subcommands = {
                AssetsCreate.class,
                AssetsImport.class,
                AssetsResolve.class,
                AssetsSearch.class,
                AssetsOrder.class,
                AssetsConsume.class},
        description = "Assets handler")
public class AssetsCommand extends NeverminedBaseCommand implements Runnable {


    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }


    public ProviderConfig serviceEndpointsBuilder()  {

        return new ProviderConfig(
                cli.getNetworkConfig().getString("gateway.url") + Constants.CONSUME_URI,
                cli.getNetworkConfig().getString("metadata-internal.url") + Constants.METADATA_URI,
                cli.getNetworkConfig().getString("gateway.url"),
                cli.getNetworkConfig().getString("metadata-internal.url") + Constants.PROVENANCE_URI,
                cli.getNetworkConfig().getString("secretstore.url"),
                cli.getNetworkConfig().getString("provider.address")
        );

    }

}
