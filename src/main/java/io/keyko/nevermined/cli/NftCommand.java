package io.keyko.nevermined.cli;

import io.keyko.nevermined.cli.modules.nft.NftBalance;
import io.keyko.nevermined.cli.modules.nft.NftBurn;
import io.keyko.nevermined.cli.modules.nft.NftMint;
import io.keyko.nevermined.cli.modules.nft.NftTransfer;
import picocli.CommandLine;

@CommandLine.Command(
        name = "nft",
        subcommands = {NftMint.class, NftBurn.class, NftBalance.class, NftTransfer.class},
        description = "Allows to manage the NFT's associated with a DID.")
public class NftCommand extends NeverminedBaseCommand implements Runnable {

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }
}
