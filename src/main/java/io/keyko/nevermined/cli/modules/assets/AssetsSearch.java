package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.cli.AssetsCLI;
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
    AssetsCLI parent;

    @CommandLine.Spec
    public CommandLine.Model.CommandSpec spec;


    @CommandLine.Parameters(index = "0")
    String query;

    @CommandLine.Option(names = { "-o", "--offset" }, required = false, description = "search offset")
    int offset= 10;

    @CommandLine.Option(names = { "-p", "--page" }, required = false, description = "page to show")
    int page= 1;

    CommandResult search() throws CLIException {

        SearchResult searchResult;
        try {
            parent.spec.commandLine().getOut().println("Searching for: " + query);

            parent.cli.progressBar.start();

            searchResult= parent.cli.getNeverminedAPI().getAssetsAPI()
                    .search(query, offset, page);

            parent.spec.commandLine().getOut().println("\nTotal results: " + searchResult.total_results
                    + " - page: " + searchResult.page
                    + " - total pages: " + searchResult.total_pages);

            searchResult.getResults().forEach( ddo -> printSimplifiedDDO(ddo));

        } catch (DDOException e) {
            throw new CLIException("Error with DDO " + e.getMessage());

        } finally {
            parent.cli.progressBar.doStop();
        }

        return CommandResult.successResult().setResult(searchResult);

    }

    private void printSimplifiedDDO(DDO ddo)    {
        parent.spec.commandLine().getOut().println("{" +
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
