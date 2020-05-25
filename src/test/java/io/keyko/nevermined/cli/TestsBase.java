package io.keyko.nevermined.cli;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TestsBase {

    protected static final Logger logger = LogManager.getLogger(TestsBase.class);
    protected static final Config config = ConfigFactory.load();

    public static final String TESTS_CONFIG_FOLDER = "src/test/resources/";
    public static final String TESTS_MAIN_CONFIG_FILE = "src/test/resources/application.conf";
    public static final String TESTS_NETWORK_FOLDER = "src/test/resources/";

}
