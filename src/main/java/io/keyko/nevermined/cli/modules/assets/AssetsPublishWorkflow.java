package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DDOException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.asset.AssetMetadata;
import picocli.CommandLine;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "publish-workflow",
        description = "Publish a new workflow")
public class AssetsPublishWorkflow extends AssetsBuilder implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;


//    ncli assets publish-workflow --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor \
//            --container python:3.8-alpine --inputs did:nv:123,did:nv:456 --transformation did:nv:abc


    @CommandLine.Option(names = { "-t", "--title" }, required = true, description = "wordcount")
    String title;

    @CommandLine.Option(names = { "-d", "--dateCreated" }, required = true,
            description = "when the file was created (format: " + DDO.DATE_PATTERN + ", example: 2012-10-10T17:00:000Z)")
    String dateCreated;

    @CommandLine.Option(names = { "-a", "--author" }, required = true, description = "the author of the file/s")
    String author;

    @CommandLine.Option(names = { "-i", "--inputs" }, required = true, description = "the input DIDs in order. It could be a comma separated list ")
    String inputs;

    @CommandLine.Option(names = { "--transformation" }, required = true, description = "the DID of the algorithm")
    String transformation;

    @CommandLine.Option(names = { "--container" }, required = true, description = "the docker container where the algorithm will be executed. Example: python:3.8-alpine")
    String container;

    final String DEFAULT_PRICE = "0";
    final String DEFAULT_LICENSE = "No License";
    final String DEFAULT_CONTENT_TYPE = "";

    CommandResult publish() throws CLIException {

        DDO ddo;
        try {
            command.printHeader("Publishing a new workflow:");

            command.cli.progressBar.start();

            AssetMetadata assetMetadata = assetMetadataBuilder(title, dateCreated, author, DEFAULT_LICENSE, DEFAULT_PRICE, Arrays.asList(), DEFAULT_CONTENT_TYPE);
            assetMetadata = workflowMetadataBuilder(assetMetadata, inputs, transformation, container);

            ddo = command.cli.getNeverminedAPI().getAssetsAPI()
                        .create(assetMetadata, command.serviceEndpointsBuilder());

            command.printSuccess();
            command.println("Workflow published: " + command.getItem(ddo.getDid().toString()));

        } catch (ParseException e) {
            command.printError("Error parsing date. Expected format: " + DDO.DATE_PATTERN);
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (DDOException | DIDFormatException e) {
            command.printError("Error with DDO/DID");
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
