package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.modules.nft.NFTBalance;
import io.keyko.nevermined.cli.modules.nft.NFTBurn;
import io.keyko.nevermined.cli.modules.nft.NFTMint;
import io.keyko.nevermined.cli.modules.nft.NFTTransfer;
import picocli.CommandLine;

@CommandLine.Command(
        name = "nft",
        subcommands = {NFTMint.class, NFTBurn.class, NFTBalance.class, NFTTransfer.class},
        description = "Allows to manage the NFT's associated with a DID.")
public class NFTCommand extends NeverminedBaseCommand implements Runnable {

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }
}
