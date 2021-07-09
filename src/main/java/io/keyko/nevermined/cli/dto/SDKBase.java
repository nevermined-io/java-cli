package io.keyko.nevermined.cli.dto;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.keyko.common.web3.KeeperService;
import io.keyko.nevermined.api.NeverminedAPI;
import io.keyko.nevermined.api.config.NeverminedConfig;
import io.keyko.nevermined.cli.helpers.Constants;
import io.keyko.nevermined.cli.helpers.ProgressBar;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.exceptions.InitializationException;
import io.keyko.nevermined.exceptions.InvalidConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

import java.io.*;
import java.util.Properties;

public class SDKBase {

    private static final Logger log = LogManager.getLogger(SDKBase.class);

    private Config mainConfig;
    private Config networkConfig= null;
    private String networkName;
    private String mainAddress;
    private NeverminedAPI neverminedAPI = null;

    public ProgressBar progressBar= new ProgressBar();


    public SDKBase() throws CLIException {
        this(Constants.CONFIG_FOLDER);
    }

    public SDKBase(String configFolder) throws CLIException {

        final String pathMainConfigFile = configFolder + File.separator + "application.conf";
        final String pathNetworkFolder = configFolder +  File.separator + "networks";

        try {
            initializeBaseConfig(configFolder);

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
                configureContractAddressesFromArtifacts(networkName, networkFile);
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

        try {
            mainAddress = mainConfig.getString("account.main.address");
            String credentialsFile = mainConfig.getString("account.main.credentialsFile");
            if (null == mainAddress || null == credentialsFile) {
                String errorMessage = "We couldn'f find a proper account configuration";
                printNoValidAccountMessage(errorMessage);
            } else if (!fileExists(credentialsFile))   {
                String errorMessage= "We couldn'f find the credentials file in the path given: " + credentialsFile;
                printNoValidAccountMessage(errorMessage);
            } else if (mainAddress.length() != 42) {
                String errorMessage= "Invalid account provided in the configuration: " + mainAddress;
                printNoValidAccountMessage(errorMessage);
            }
        } catch (Exception ex)  {
            String errorMessage= "Unexpected error: " + ex.getMessage();
            printNoValidAccountMessage(errorMessage);
            throw new CLIException(errorMessage);
        }
    }

    private void printNoValidAccountMessage(String errorMessage)    {
        log.error("\nWARNING:\nIt looks the existing configuration doesn't include a valid account. " +
                "You can create a new account running:\n" +
                "ncli accounts new -m --password PASSWORD\n\n" +
                "That command will create a new account to interact with the network and will " +
                "leave everything ready in your configuration.\n");
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

    public Config getNetworkConfig()    {
        return networkConfig;
    }

    public Config getMainConfig()    {
        return mainConfig;
    }

    public String getMainAddress() {
        return mainAddress;
    }

    private boolean fileExists(String path)    {
        File configFile = new File(path);
        return configFile.exists();
    }

    private boolean initializeBaseConfig(final String configFolder) throws CLIException {
        if (!fileExists(configFolder))    {
            boolean success = (new File(configFolder)).mkdirs();
            if (!success) {
                log.error("Unable to create main config folder " + configFolder);
                throw new CLIException("Unable to create main config folder " + configFolder);
            }
        }

        final String accountsFolder = configFolder + File.separator + "accounts" + File.separator;
        final String logsConfigFile = configFolder + File.separator + "log4j2.properties";
        final String mainConfigFile = configFolder + File.separator + "application.conf";

        if (!fileExists(accountsFolder))    {
            boolean success = (new File(accountsFolder)).mkdirs();
        }

        if (!fileExists(logsConfigFile))    {
            copyResourceFileToPath("src/main/resources/log4j2.properties", logsConfigFile);
        }

        if (!fileExists(mainConfigFile))    {
            copyResourceFileToPath("src/main/resources/application.conf", mainConfigFile);
        }
        return true;
    }

    public Credentials getCredentials() throws IOException, CipherException {
        return KeeperService.getInstance(
                networkConfig.getString(NeverminedConfig.KEEPER_URL),
                mainConfig.getString(NeverminedConfig.MAIN_ACCOUNT_ADDRESS),
                mainConfig.getString(NeverminedConfig.MAIN_ACCOUNT_PASSWORD),
                mainConfig.getString(NeverminedConfig.MAIN_ACCOUNT_CREDENTIALS_FILE),
                networkConfig.getInt(NeverminedConfig.KEEPER_TX_ATTEMPTS),
                networkConfig.getLong(NeverminedConfig.KEEPER_TX_SLEEPDURATION)
        ).getCredentials();
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

    public static boolean configureContractAddressesFromArtifacts(String networkName, String confFile)  {
        try {
            log.info("Loading contract addresses of " + networkName + " network into " + confFile);
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(
                    "bash",
                    "src/main/bash/updateConfAddresses.sh",
                    networkName,
                    confFile);
            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                log.info("Network addresses copied to config file");
                return true;
            } else {
                log.error("Unable to generate configuration");
            }
            return false;
        } catch (IOException | InterruptedException e) {
            log.error("Unable to parse network config");
            log.error(e.getMessage());
        }
        return false;
    }

}
