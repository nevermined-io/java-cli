# Running the Nevermined CLI

## Configuring the shell alias

If you compiled the CLI project, you will find JAR binary package in the `target` folder.
You can run the application using the usual way (`java -jar app.jar`).
Also, setting up an alias would make quicker and easier to run the application:

```bash
alias ncli='java $NEVERMINED_OPTS -jar target/cli-*-shaded.jar'
```

But if you are using the Docker container, this command can facilitate running the CLI from the host:

```bash
alias ncli="docker exec -it nevermined-cli java -jar cli-shaded.jar $NEVERMINED_OPTIONS"
```

After of that you should be able to run the CLI and see something like the following:

```bash
$ ncli

WARNING:
It looks the existing configuration doesn't include a valid account. You can create a new account running:
ncli accounts new -m --password PASSWORD

That command will create a new account to interact with the network and will leave everything ready in your configuration.


~ Welcome to Nevermined CLI ~

Usage: ncli [-hV] [COMMAND]
Prints usage help and version help when requested.

  -h, --help      Displays this help message and quits.
  -V, --version   Display version info
Commands:
  config      Reading and setting application config
  assets      Assets handler
  compute     Compute handler
  provenance  W3C Provenance
  accounts    Allowing to interact with the accounts.
  tokens      Allows to request Tokens and transfer to other accounts.
  nft         Allows to manage the NFT's associated with a DID.
  utils       Utils interface
  contract    Nevermined Smart Contracts interface

built by Keyko (https://keyko.io)

```


## Connecting to a different network

All the parameters can be modified in the config files found in `~/.local/share/nevermined-cli`. Also it's possible
to setup the **NEVERMINED_OPTS** environment variable overriding the options by default.

For example, connecting to a different network is so simple as setting up the following environment variable and running
 again the ncli:

```bash
export NEVERMINED_OPTS=" $NEVERMINED_OPTS -Dnetwork=production"
```

## Using a different logging config file

```bash
export NEVERMINED_OPTS=" $NEVERMINED_OPTS -Dlog4j.configurationFile=$HOME/.local/share/nevermined-cli/log4j2.properties"
```
