package io.keyko.nevermined.cli.helpers;

import java.io.File;

public abstract class Constants {

    public static final String CONFIG_FOLDER = System.getProperty("user.home") + File.separator + ".local" + File.separator + "share" + File.separator + "nevermined-cli";

    public static final String NETWORK_FOLDER = CONFIG_FOLDER + File.separator + "networks" + File.separator;

    public static final String MAIN_CONFIG_FILE = CONFIG_FOLDER + File.separator + "application.conf";

    public static final String LOGS_CONFIG_FILE = CONFIG_FOLDER + File.separator + "log4j2.properties";

    public static final String ACCOUNTS_FOLDER = CONFIG_FOLDER + File.separator + "accounts" + File.separator;


    public static final String TRANSACTION_SUCCESS = "0x1";

    public static final String METADATA_URI = "/api/v1/metadata/assets/ddo/{did}";

    public static final String PROVENANCE_URI = "/api/v1/metadata/assets/ddo/{did}";

    public static final String CONSUME_URI = "/api/v1/gateway/services/access";

}
