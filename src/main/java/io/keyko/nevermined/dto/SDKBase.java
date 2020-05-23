package io.keyko.nevermined.dto;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.keyko.nevermined.api.NeverminedAPI;
import io.keyko.nevermined.cli.helpers.Constants;
import io.keyko.nevermined.cli.helpers.ProgressBar;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.InitializationException;
import io.keyko.nevermined.exceptions.InvalidConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SDKBase {

    private static final Logger log = LogManager.getLogger(SDKBase.class);

    private static Config mainConfig;
    private static Config networkConfig= null;
    private static String networkName;
    private static NeverminedAPI neverminedAPI = null;

    public ProgressBar progressBar= new ProgressBar();


    public SDKBase() throws CLIException {
        this(Constants.configFolder);
    }

    public SDKBase(String configFolder) throws CLIException {

        final String pathMainConfigFile = configFolder + File.separator + "application.conf";
        final String pathNetworkFolder = configFolder +  File.separator + "networks";

        try {
            initializeBaseConfig();

            mainConfig= ConfigFactory.parseFile(new File(pathMainConfigFile));

            progressBar.setSpinner(mainConfig.getInt("spinner.id"));

        } catch (Exception e) {
            log.error("Unable load the main config file: " + e.getMessage());
            throw new CLIException("Unable load the main config file: " + e.getMessage());
        }

        try {

            networkName= mainConfig.getString("network");
            String networkFile= pathNetworkFolder + File.separator + networkName + ".conf";
            if (!fileExists(networkFile))    {
                copyResourceFileToPath("src/main/resources/networks/" + networkName + ".conf", networkFile);
            }

            if (!fileExists(networkFile))    {
                log.error("Unable to load contract config file " + networkFile);
                log.error("Please, copy the config file to the " + networkFile + " path");
                throw new CLIException("Unable load the contract config file: " + networkFile);
            }

            networkConfig= ConfigFactory.parseFile(new File(networkFile));

        } catch (Exception e) {
            throw new CLIException("Unable to initialize Nevermined connections: " + e.getMessage());
        }
    }

    public NeverminedAPI getNeverminedAPI() throws CLIException {
        if (neverminedAPI == null)  {
            try {
                neverminedAPI = NeverminedAPI.getInstance(joinConfig(networkConfig, mainConfig));
            } catch (InitializationException | InvalidConfiguration ex) {
                throw new CLIException("Unable to initialize Nevermined connections: " + ex.getMessage());
            }
        }
        return neverminedAPI;
    }

    public static Config getNetworkConfig()    {
        return networkConfig;
    }

    public static Config getMainConfig()    {
        return mainConfig;
    }

    private boolean fileExists(String path)    {
        File configFile = new File(path);
        return configFile.exists();
    }

    private boolean initializeBaseConfig() throws CLIException {
        if (!fileExists(Constants.configFolder))    {
            boolean success = (new File(Constants.configFolder)).mkdirs();
            if (!success) {
                log.error("Unable to create main config folder " + Constants.configFolder);
                throw new CLIException("Unable to create main config folder " + Constants.configFolder);
            }

        }

        if (!fileExists(Constants.accountsFolder))    {
            boolean success = (new File(Constants.accountsFolder)).mkdirs();
        }

        if (!fileExists(Constants.logsConfigFile))    {
            copyResourceFileToPath("src/main/resources/log4j2.properties", Constants.logsConfigFile);
        }

        if (!fileExists(Constants.mainConfigFile))    {
            copyResourceFileToPath("src/main/resources/application.conf", Constants.mainConfigFile);
        }
        return true;
    }

    private static Properties joinConfig(Config c1, Config c2) {
        Properties properties = new Properties();
        c1.entrySet().forEach(e -> properties.setProperty(e.getKey(), c1.getString(e.getKey())));
        c2.entrySet().forEach(e -> properties.setProperty(e.getKey(), c2.getString(e.getKey())));
        return properties;
    }




    public static boolean copyResourceFileToPath(String inputPath, String outputPath) {
        try {
            log.info("Copying " + inputPath + " to " + outputPath);

            InputStream initialStream = FileUtils.openInputStream(new File(inputPath));
            File targetFile = new File(outputPath);
            FileUtils.copyInputStreamToFile(initialStream, targetFile);
            return true;
        } catch (IOException e) {
            log.error("Unable to copy " + inputPath + " to " + outputPath);
            log.error(e.getMessage());
        }
        return false;
    }

}
