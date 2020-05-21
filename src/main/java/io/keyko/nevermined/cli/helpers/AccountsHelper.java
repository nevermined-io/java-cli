package io.keyko.nevermined.cli.helpers;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public abstract class AccountsHelper {

    public static String createAccount(String password, String destination) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        return WalletUtils.generateNewWalletFile(password, new File(destination));
    }

    public static String getAddressFromFilePath(String walletFileName)    {
        try {
            String[] fetchAddress = walletFileName.split("--");
            return "0x" + fetchAddress[fetchAddress.length - 1].split("\\.")[0];
        } catch (Exception e)   {
            return null;
        }
    }


}
