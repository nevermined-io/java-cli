package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DDOException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.asset.AssetMetadata;
import io.keyko.nevermined.models.service.Service;
import picocli.CommandLine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "create",
        description = "Create an Asset")
public class AssetsCreate implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;


    // $ ncli assets create --title title --dateCreated 2012-10-10T17:00:000Z
    // --author aitor --license CC-BY --contentType text/csv --price 10
    // --url https://google.com/robots.txt

    @CommandLine.Option(names = { "-t", "--title" }, required = true, description = "the asset title")
    String title;

    @CommandLine.Option(names = { "-d", "--dateCreated" }, required = true,
            description = "when the file was created (format: " + DDO.DATE_PATTERN + ", example: 2012-10-10T17:00:000Z)")
    String dateCreated;

    @CommandLine.Option(names = { "-a", "--author" }, required = true, description = "the author of the file")
    String author;

    @CommandLine.Option(names = { "-l", "--license" }, required = false,
            defaultValue = "No License Specified", description = "license of the asset, default value: ${DEFAULT-VALUE}")
    String license;

    @CommandLine.Option(names = { "-c", "--contentType" }, required = true, description = "file content type")
    String contentType;

    @CommandLine.Option(names = { "-p", "--price" }, required = true, description = "the asset price")
    String price;

    @CommandLine.Option(names = { "-u", "--url" }, required = true, description = "the asset url")
    String url;

    CommandResult create() throws CLIException {

        DDO ddo;
        try {
            command.printHeader("Creating a new asset:");

            command.cli.progressBar.start();

            ddo = command.cli.getNeverminedAPI().getAssetsAPI()
                    .create(assetMetadataBuilder(), command.serviceEndpointsBuilder());

            command.printSuccess();
            command.println("Asset Created: " + command.getItem(ddo.getDid().toString()));

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

    AssetMetadata assetMetadataBuilder() throws ParseException {
        AssetMetadata metadata= AssetMetadata.builder();

        metadata.attributes.main.name= title;
        metadata.attributes.main.type= Service.AssetTypes.dataset.toString();
        metadata.attributes.main.dateCreated= new SimpleDateFormat(DDO.DATE_PATTERN, Locale.ENGLISH).parse(dateCreated);
        metadata.attributes.main.author= author;
        metadata.attributes.main.license= license;
        metadata.attributes.main.price= price;
        ArrayList<AssetMetadata.File> files= new ArrayList<>();
        AssetMetadata.File file = new AssetMetadata.File();
        file.index= 0;
        file.url= url;
        file.contentType= contentType;
        files.add(file);
        metadata.attributes.main.files = files;

        return metadata;
    }


    @Override
    public Object call() throws CLIException {
        return create();
    }
}
