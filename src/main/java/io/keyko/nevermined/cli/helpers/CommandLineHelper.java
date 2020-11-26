package io.keyko.nevermined.cli.helpers;

import org.json.JSONObject;
import org.web3j.crypto.Hash;
import org.web3j.crypto.WalletUtils;

import java.util.UUID;

public abstract class CommandLineHelper {

    public static final String PARAM_PROVENANCE_ID_DESC = "Unique Identifier of the provenance event, if this is not given, a random ID will be generated";

    public static final String PARAM_AGENT_DESC = "Ethereum address of the user associated to the provenance event";

    public static final String PARAM_ACTIVITY_DESC = "Ethereum address of the user associated to the provenance event";

    public static final String PARAM_ATTRIBUTES_DESC =  "Additional attributes or information associated to the event";

    public static String generateProvenanceIdIfEmpty(String provenanceId)   {
        if (null == provenanceId || provenanceId.isEmpty())
            return generateRandomProvenanceID();
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

    public static String generateRandomProvenanceID() {
        String token = UUID.randomUUID().toString() + UUID.randomUUID().toString();
        return token.replaceAll("-", "");
    }

    public static String prettyJson(String json)    {
        return new JSONObject(json).toString(4);
    }
}
