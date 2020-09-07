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
        name = "publish-algorithm",
        description = "Publish a new algorithm")
public class AssetsPublishAlgorithm extends AssetsBuilder implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;


//    ncli assets publish-algorithm --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor --contentType text/text \
//            --price 0 --language python --entrypoint "python word_count.py" --container python:3.8-alpine \
//            --url https://raw.githubusercontent.com/keyko-io/nevermined-sdk-py/examples/word_count.py

    @CommandLine.Option(names = { "-t", "--title" }, required = true, description = "the algorithm title")
    String title;

    @CommandLine.Option(names = { "-d", "--dateCreated" }, required = true,
            description = "when the file was created (format: " + DDO.DATE_PATTERN + ", example: 2012-10-10T17:00:000Z)")
    String dateCreated;

    @CommandLine.Option(names = { "-a", "--author" }, required = true, description = "the author of the file/s")
    String author;

    @CommandLine.Option(names = { "-l", "--license" }, required = false,
            defaultValue = "No License Specified", description = "license of the algorithm, default value: ${DEFAULT-VALUE}")
    String license;

    @CommandLine.Option(names = { "-c", "--contentType" }, required = true, description = "file content type")
    String contentType;

    @CommandLine.Option(names = { "-p", "--price" }, required = false, defaultValue = "0", description = "the algorithm price")
    String price;

    @CommandLine.Option(names = { "-u", "--url" }, required = true, description = "the asset url to the algorithm")
    String url;

    @CommandLine.Option(names = { "--language" }, required = true, description = "the programing language of the algorithm")
    String language;

    @CommandLine.Option(names = { "-e", "--entrypoint" }, required = true, description = "the entrypoint for running the algorithm. Example: python word_count.py")
    String entrypoint;

    @CommandLine.Option(names = { "--container" }, required = true, description = "the docker container where the algorithm can be executed. Example: python:3.8-alpine")
    String container;



    CommandResult publish() throws CLIException {

        DDO ddo;
        try {
            command.printHeader("Publishing a new algorithm:");

            command.cli.progressBar.start();

            AssetMetadata assetMetadata = assetMetadataBuilder(title, dateCreated, author, license, price, Arrays.asList(url), contentType);
            assetMetadata = algorithmMetadataBuilder(assetMetadata, language, entrypoint, container);

            ddo = command.cli.getNeverminedAPI().getAssetsAPI()
                    .create(assetMetadata, command.serviceEndpointsBuilder());


            command.printSuccess();
            command.println("Algorithm published: " + command.getItem(ddo.getDid().toString()));

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
