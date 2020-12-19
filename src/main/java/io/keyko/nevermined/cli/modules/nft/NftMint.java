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
        name = "mint",
        description = "Allows a DID owner to mint a NFT associated with the DID")
public class NftMint implements Callable {

    @CommandLine.ParentCommand
    NftCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Parameters(index = "1")
    BigInteger amount;

    CommandResult mint() throws CLIException {
        try {
            command.printHeader("Minting NFT's associated to a DID:");
            command.println("DID: " + did +
                    "\nAmount to mint: " + amount.longValue());

            command.cli.progressBar.start();

            boolean status= command.cli.getNeverminedAPI().getAssetsAPI()
                    .mint(new DID(did), amount);

            if (status)
                command.printSuccess();
        } catch (DIDFormatException e) {
            command.printError("Invalid DID");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (NftException e) {
            command.printError("Error minting NFT");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult();    }

    @Override
    public CommandResult call() throws CLIException {
        return mint();
    }
}
