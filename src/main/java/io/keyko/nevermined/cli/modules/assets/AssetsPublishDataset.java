package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DDOException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.asset.AssetMetadata;
import picocli.CommandLine;

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

    @CommandLine.Option(names = { "-s", "--service" }, required = true, description = "access or compute", defaultValue = "access")
    String service;

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

    @CommandLine.Option(names = { "-u", "--urls" }, description = "the asset urls. It could be a comma separated list of urls")
    String urls;

    CommandResult publish() throws CLIException {

        DDO ddo;
        try {
            command.printHeader("Publishing a new dataset:");

            command.cli.progressBar.start();

            final List<String> listUrls = Arrays.asList(urls.split(","));

            final AssetMetadata assetMetadata = assetMetadataBuilder(title, dateCreated, author, license, price, listUrls, contentType);

            if (service.toLowerCase().equals("compute"))    {
                ddo = command.cli.getNeverminedAPI().getAssetsAPI()
                        .createComputeService(assetMetadata, command.serviceEndpointsBuilder());

            } else if (service.toLowerCase().equals("access"))    {
                ddo = command.cli.getNeverminedAPI().getAssetsAPI()
                        .create(assetMetadata, command.serviceEndpointsBuilder());

            }   else {
                command.printError("The service has to be access or compute");
                return CommandResult.errorResult();
            }


            command.printSuccess();
            command.println("Dataset published: " + command.getItem(ddo.getDid().toString()));

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
