package io.keyko.nevermined.cli.helpers;

import io.keyko.common.exceptions.EncodingException;
import io.keyko.common.helpers.CryptoHelper;
import io.keyko.common.helpers.EncodingHelper;
import io.keyko.common.helpers.EthereumHelper;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.crypto.WalletUtils;

import java.util.UUID;

public abstract class CommandLineHelper {

    public static final String PARAM_PROVENANCE_ID_DESC = "Unique Identifier of the provenance event, if this is not given, a random ID will be generated";

    public static final String PARAM_AGENT_DESC = "Ethereum address of the user associated to the provenance event";

    public static final String PARAM_ACTIVITY_DESC = "Ethereum address of the user associated to the provenance event";

    public static final String PARAM_ATTRIBUTES_DESC =  "Additional attributes or information associated to the event";


    public static String signMessage(String message, Credentials credentials)   {
        Sign.SignatureData signatureData = EthereumHelper.signMessage(message, credentials.getEcKeyPair());
        return EncodingHelper.signatureToString(signatureData);
    }

    public static boolean isValidSignature(String message, String signature, String address)    {
        try {
            byte[] hashMessage= EthereumHelper.getEthereumMessageHash(message);
            Sign.SignatureData signatureGenerated =
                    EncodingHelper.stringToSignature(EthereumHelper.remove0x(signature));
            return EthereumHelper.recoverAddressFromSignature(signatureGenerated, hashMessage)
                    .contains(address.toLowerCase());
        } catch (EncodingException e) {
            return false;
        }
    }

    public static String generateProvenanceIdIfEmpty(String provenanceId)   {
        if (null == provenanceId || provenanceId.isEmpty())
            return generateRandomIdentifier();
        return provenanceId;
    }

    public static String generateActivityIfEmpty(String activity, String description)   {
        if (null == activity || activity.isEmpty())
            return Hash.sha3String(description);
        return Hash.sha3String(activity);
    }

    public static boolean isAddressFormatValid(String address)  {
        return WalletUtils.isValidAddress(address);
    }

    public static String generateRandomIdentifier() {
        String token = UUID.randomUUID().toString() + UUID.randomUUID().toString();
        return token.replaceAll("-", "");
    }

    public static String prettyJson(String json)    {
        return new JSONObject(json).toString(4);
    }
}
