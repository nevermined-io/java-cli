package io.keyko.nevermined.cli.modules.assets;

import com.fasterxml.jackson.core.type.TypeReference;
import io.keyko.nevermined.api.helper.AccountsHelper;
import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DDOException;
import io.keyko.nevermined.models.AssetRewards;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.asset.AssetMetadata;
import io.keyko.nevermined.models.service.ProviderConfig;
import io.keyko.nevermined.models.service.ServiceDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "import",
        description = "Import an asset using the metadata in JSON format ")
public class AssetsImport extends AssetsBuilder implements Callable {

    private static final Logger log = LogManager.getLogger(AssetsImport.class);

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String metadataFile;

    @CommandLine.Option(names = { "-s", "--services" }, required = true, description = "comma separated list of services to attach to the new asset", defaultValue = "access")
    String services;

    @CommandLine.Option(names = { "-p", "--price" }, defaultValue = "0", description = "the dataset price")
    String price;

    @CommandLine.Option(names = { "--cap" }, defaultValue = "0", description = "minting cap")
    String cap;

    @CommandLine.Option(names = { "--royalties" }, defaultValue = "0", description = "royalties in the secondary market (between 0 and 100)")
    String royalties;

    @CommandLine.Option(names = { "--tokenAddress" }, defaultValue = AccountsHelper.ZERO_ADDRESS, description = "contract address of the ERC20 contract to use. If not given the payment can be made in ETH")
    String tokenAddress;

    CommandResult importAsset() throws CLIException {

        DDO ddo = null;
        try {
            command.printHeader("Importing asset");
            command.printSubHeader("Using metadata from file " + metadataFile);

            command.cli.progressBar.start();

            final AssetMetadata assetMetadata = assetMetadataBuilder(metadataFile);
            final List<String> listServices = Arrays.asList(services.split(","));
            final ProviderConfig providerConfig = command.serviceEndpointsBuilder();

            AssetRewards assetRewards;

            if (new BigInteger(price).compareTo(BigInteger.ZERO) >0)
                assetRewards = new AssetRewards(command.cli.getMainAddress(), price);
            else
                assetRewards = new AssetRewards();
            assetRewards.tokenAddress = tokenAddress;

            List<ServiceDescriptor> descriptors = buildServicesDescriptors(
                    command.cli.getNetworkConfig(),
                    providerConfig,
                    listServices,
                    assetRewards,
                    command.cli.getMainAddress());

            ddo = command.cli.getNeverminedAPI().getAssetsAPI().create(
                    assetMetadata, descriptors, providerConfig, new BigInteger(cap), new BigInteger(royalties));

            command.printSuccess();
            command.println("Asset Created: " + command.getItem(ddo.getDID().did));

        } catch (IOException e) {
            command.printError("Error parsing metadata");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (DDOException e) {
            command.printError("Error with DDO");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult().setResult(ddo);

    }


    AssetMetadata assetMetadataBuilder(String filePath) throws IOException {
        String jsonContent =  new String(Files.readAllBytes(Paths.get(filePath)));
        return DDO.fromJSON(new TypeReference<AssetMetadata>() {}, jsonContent);
    }

    @Override
    public Object call() throws CLIException {
        return importAsset();
    }
}
