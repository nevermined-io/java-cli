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
        name = "balance",
        description = "Gets the balance of the NFT associated to a DID")
public class NftBalance implements Callable {

    @CommandLine.ParentCommand
    NftCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Parameters(index = "1")
    String address;

    CommandResult balance() throws CLIException {
        try {
            command.printHeader("Getting balance of NFT's associated to a DID:");
            command.println("DID: " + did +
                    "\nAddress: " + address);

            command.cli.progressBar.start();

            BigInteger balance = command.cli.getNeverminedAPI().getAssetsAPI()
                    .balance(address, new DID(did));

            command.printSuccess();
            return CommandResult.successResult().setMessage(balance.toString());

        } catch (DIDFormatException e) {
            command.printError("Invalid DID");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (NftException e) {
            command.printError("Error getting NFT balance");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
    }

    @Override
    public CommandResult call() throws CLIException {
        return balance();
    }
}
