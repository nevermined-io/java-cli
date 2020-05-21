package io.keyko.nevermined.cli.helpers;

import org.json.JSONObject;

public abstract class CommandLineHelper {

    public static String prettyJson(String json)    {
        return new JSONObject(json).toString(4);
    }
}
