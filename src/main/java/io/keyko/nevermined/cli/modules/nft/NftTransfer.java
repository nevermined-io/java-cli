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
        name = "transfer",
        description = "Allows a DID owner to transfer a specific amount of NFT associated with the DID")
public class NftTransfer implements Callable {

    @CommandLine.ParentCommand
    NftCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String did;

    @CommandLine.Parameters(index = "1")
    String address;

    @CommandLine.Parameters(index = "2")
    BigInteger amount;

    CommandResult transfer() throws CLIException {
        try {
            command.printHeader("Transferring NFT's associated to a DID:");
            command.println("DID: " + did +
                    "\nTo address: " + address +
                    "\nAmount to transfer: " + amount.longValue());

            command.cli.progressBar.start();

            boolean status= command.cli.getNeverminedAPI().getAssetsAPI()
                    .transfer(new DID(did), address,  amount);

            if (status)
                command.printSuccess();
        } catch (DIDFormatException e) {
            command.printError("Invalid DID");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } catch (NftException e) {
            command.printError("Error transferring NFT's");
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }
        return CommandResult.successResult();    }

    @Override
    public CommandResult call() throws CLIException {
        return transfer();
    }
}
