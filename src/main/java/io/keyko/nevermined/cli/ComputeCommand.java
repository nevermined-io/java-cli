package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.helpers.Constants;
import io.keyko.nevermined.cli.modules.compute.ComputeLogs;
import io.keyko.nevermined.cli.modules.compute.ComputeStatus;
import io.keyko.nevermined.models.service.ProviderConfig;
import picocli.CommandLine;

@CommandLine.Command(
        name = "compute",
        subcommands = {
                ComputeLogs.class,
                ComputeStatus.class
        },
        description = "Compute handler"
)

public class ComputeCommand extends NeverminedBaseCommand implements Runnable {

    @Override
    public void run() { spec.commandLine().usage(System.out); }

    public ProviderConfig serviceEndpointsBuilder()  {

        return new ProviderConfig(
                cli.getNetworkConfig().getString("gateway.url") + Constants.ACCESS_URI,
                cli.getNetworkConfig().getString("metadata-internal.url") + Constants.METADATA_URI,
                cli.getNetworkConfig().getString("gateway.url"),
                cli.getNetworkConfig().getString("metadata-internal.url") + Constants.PROVENANCE_URI,
                cli.getNetworkConfig().getString("secretstore.url"),
                cli.getNetworkConfig().getString("provider.address")
        )
                .setExecuteEndpoint(cli.getNetworkConfig().getString("gateway.url") + Constants.EXECUTE_URI);

    }
}
