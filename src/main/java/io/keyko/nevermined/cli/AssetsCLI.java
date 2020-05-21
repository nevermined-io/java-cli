package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.cli.helpers.Constants;
import io.keyko.nevermined.cli.modules.assets.*;
import io.keyko.nevermined.models.service.ProviderConfig;
import picocli.CommandLine;

import java.util.concurrent.Callable;

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
public class AssetsCLI extends NeverminedCommand implements Callable {

    @CommandLine.ParentCommand
    public NeverminedCLI cli;

    @CommandLine.Spec
    public CommandLine.Model.CommandSpec spec;


    @Override
    public Object call() {
        spec.commandLine().usage(System.out);
        return null;
    }

    public ProviderConfig serviceEndpointsBuilder()  {

        return new ProviderConfig(
                cli.getNetworkConfig().getString("gateway.url") + Constants.consumeUri,
                cli.getNetworkConfig().getString("gateway.url") + Constants.initializeUri,
                cli.getNetworkConfig().getString("metadata-internal.url") + Constants.metadataUri,
                cli.getNetworkConfig().getString("secretstore.url"),
                cli.getNetworkConfig().getString("provider.address")
        );

    }

}
