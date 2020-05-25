package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCommand;
import io.keyko.nevermined.cli.helpers.Logger;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DDOException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.metadata.SearchResult;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "search",
        description = "Searching for assets")
public class AssetsSearch implements Callable {

    @CommandLine.ParentCommand
    AssetsCommand command;

    @CommandLine.Mixin
    Logger logger;

    @CommandLine.Parameters(index = "0")
    String query;

    @CommandLine.Option(names = { "-o", "--offset" }, required = false, description = "search offset")
    int offset= 10;

    @CommandLine.Option(names = { "-p", "--page" }, required = false, description = "page to show")
    int page= 1;

    CommandResult search() throws CLIException {

        SearchResult searchResult;
        try {
            command.printHeader("Searching for:");
            command.printSubHeader(query);

            command.cli.progressBar.start();

            searchResult= command.cli.getNeverminedAPI().getAssetsAPI()
                    .search(query, offset, page);

            command.println("\nTotal results: " + command.getItem(String.valueOf(searchResult.total_results))
                    + " - page: " + command.getItem(String.valueOf(searchResult.page))
                    + " - total pages: " + command.getItem(String.valueOf(searchResult.total_pages)));

            searchResult.getResults().forEach( ddo -> printSimplifiedDDO(ddo));

        } catch (DDOException e) {
            command.printError("Error processing DDO");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();

        } finally {
            command.cli.progressBar.doStop();
        }

        return CommandResult.successResult().setResult(searchResult);

    }

    private void printSimplifiedDDO(DDO ddo)    {
        command.println("{" +
                "\n\t\"did\": \"" + ddo.id + "\", " +
                "\n\t\"title\": \"" + ddo.getMetadataService().attributes.main.name  + "\", " +
                "\n\t\"price\": \"" + ddo.getMetadataService().attributes.main.price  + "\" " +
                "\n}");
    }

    @Override
    public Object call() throws CLIException {
        return search();
    }
}
