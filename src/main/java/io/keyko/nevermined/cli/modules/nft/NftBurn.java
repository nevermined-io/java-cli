package io.keyko.nevermined.cli.modules.nft;

import io.keyko.nevermined.cli.NftCommand;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.exceptions.NftException;
import io.keyko.nevermined.models.DID;
import picocli.CommandLine;

import java.math.BigInteger;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "burn",
        description = "Allows a DID owner to burn NFT's associated with the DID")
public class NftBurn implements Callable {

    @CommandLine.ParentCommand
    NftCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Parameters(index = "1")
    BigInteger amount;

    CommandResult burn() throws CLIException {
        try {
            command.printHeader("Burning NFT's associated to a DID:");
            command.println("DID: " + did +
                    "\nAmount to burn: " + amount.longValue());

            command.cli.progressBar.start();

            boolean status= command.cli.getNeverminedAPI().getAssetsAPI()
                    .burn(new DID(did), amount);

            if (status)
                command.printSuccess();
        } catch (DIDFormatException e) {
            command.printError("Invalid DID");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (NftException e) {
            command.printError("Error burning NFT");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult();    }

    @Override
    public CommandResult call() throws CLIException {
        return burn();
    }
}
