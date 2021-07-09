package io.keyko.nevermined.cli.modules.assets;

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
import picocli.CommandLine;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "publish-dataset",
        description = "Publish a new dataset")
public class AssetsPublishDataset extends AssetsBuilder implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;


    // $ ncli assets publish-dataset --title title --dateCreated 2012-10-10T17:00:000Z
    // --author aitor --license CC-BY --contentType text/csv --price 10
    // --url https://google.com/robots.txt

    @CommandLine.Option(names = { "-s", "--services" }, required = true, description = "comma separated list of services to attach to the new asset", defaultValue = "access")
    String services;

    @CommandLine.Option(names = { "-t", "--title" }, required = true, description = "the dataset title")
    String title;

    @CommandLine.Option(names = { "-d", "--dateCreated" }, required = true,
            description = "when the file was created (format: " + DDO.DATE_PATTERN + ", example: 2012-10-10T17:00:000Z)")
    String dateCreated;

    @CommandLine.Option(names = { "-a", "--author" }, required = true, description = "the author of the file/s")
    String author;

    @CommandLine.Option(names = { "-l", "--license" }, required = false,
            defaultValue = "No License Specified", description = "license of the dataset, default value: ${DEFAULT-VALUE}")
    String license;

    @CommandLine.Option(names = { "-c", "--contentType" }, required = true, description = "file content type")
    String contentType;

    @CommandLine.Option(names = { "-p", "--price" }, defaultValue = "0", description = "the dataset price")
    String price;

    @CommandLine.Option(names = { "-u", "--urls" }, required = true, description = "the asset urls. It could be a comma separated list of urls")
    String urls;

    @CommandLine.Option(names = { "--cap" }, defaultValue = "0", description = "minting cap")
    String cap;

    @CommandLine.Option(names = { "--royalties" }, defaultValue = "0", description = "royalties in the secondary market (between 0 and 100)")
    String royalties;

    @CommandLine.Option(names = { "--tokenAddress" }, defaultValue = AccountsHelper.ZERO_ADDRESS, description = "contract address of the ERC20 contract to use. If not given the payment can be made in ETH")
    String tokenAddress;


    CommandResult publish() throws CLIException {

        DDO ddo;
        try {
            command.printHeader("Publishing a new dataset:");

            command.cli.progressBar.start();

            final List<String> listUrls = Arrays.asList(urls.split(","));
            final List<String> listServices = Arrays.asList(services.split(","));
            final ProviderConfig providerConfig = command.serviceEndpointsBuilder();

            final AssetMetadata assetMetadata = assetMetadataBuilder(title, dateCreated, author, license, price, listUrls, contentType);

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
            command.println("Dataset published: " + command.getItem(ddo.getDID().toString()));

        } catch (ParseException e) {
            command.printError("Error parsing date. Expected format: " + DDO.DATE_PATTERN);
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

    @Override
    public Object call() throws CLIException {
        return publish();
    }
}
