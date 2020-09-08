[![banner](https://raw.githubusercontent.com/keyko-io/assets/master/images/logo/nevermined_logo_1.png)](https://nevermined.io)

# Nevermined Command Line Interface (CLI)

> CLI for interacting with Nevermined Data Platform
> [nevermined.io](https://nevermined.io)

![Build](https://github.com/keyko-io/nevermined-cli/workflows/Build/badge.svg)

---

## Table of Contents


   * [Nevermined Command Line Interface (CLI)](#nevermined-command-line-interface-cli)
      * [Table of Contents](#table-of-contents)
      * [Features](#features)
         * [Compiling](#compiling)
         * [Installation](#installation)
         * [Running the Nevermined CLI](#running-the-nevermined-cli)
            * [Connecting to a different network](#connecting-to-a-different-network)
            * [Using a different logging config file](#using-a-different-logging-config-file)
      * [License](#license)

---

## Features

Command Line Interface (CLI) tool allowing to interact with the Nevermined Data Sharing platform.

### Compiling

You can compile the application using the following command:

```bash
mvn clean package
```

Nevermined CLI requires Java 11.

### Installation

After compiling the application, you will see in the `target/` folder a zip file with the config files required.
Nevermined CLI can setup the config files for you with the default parameters,
but you could also unzip the zip file into the `~/.local/share/nevermined-cli` folder, e.g.

```bash
unzip target/nevermined-cli-*.zip -d ~/.local/share/nevermined-cli
```

The unzipped folder should have the following structure:

```bash
ls -la ~/.local/share/nevermined-cli/

total 24
drwxrwxr-x  4 aitor aitor 4096 May  2 19:07 .
drwx------ 46 aitor aitor 4096 May 20 10:47 ..
drwxrwxr-x  2 aitor aitor 4096 May  2 19:07 accounts
-rw-rw-r--  1 aitor aitor  330 May  2 19:07 application.conf
-rw-rw-r--  1 aitor aitor  489 May  2 19:07 log4j2.properties
drwxrwxr-x  2 aitor aitor 4096 May  2 19:07 networks


```

### Running the Nevermined CLI

This should generate the JAR package in the target folder. You can run the application using the usual way (`java -jar app.jar`). 
Also, setting up an alias would make quicker and easier to run the application:

```bash
alias ncli='java $NEVERMINED_OPTS -jar target/cli-*-shaded.jar'
```

#### Connecting to a different network

All the parameters can be modified in the config files found in `~/.local/share/nevermined-cli`. Also it's possible 
to setup the **NEVERMINED_OPTS** environment variable overriding the options by default.

For example, connecting to a different network is so simple as setting up the following environment variable and running
 again the ncli:

```bash
export NEVERMINED_OPTS=" $NEVERMINED_OPTS -Dnetwork=production"
``` 

#### Using a different logging config file

```bash
export NEVERMINED_OPTS=" $NEVERMINED_OPTS -Dlog4j.configurationFile=$HOME/.local/share/nevermined-cli/log4j2.properties"
``` 


```
# Show version information
ncli --version

# Show config information
ncli config show

# Clean previous configuration parameters
ncli config clean

# Show help
ncli --help

# Create a new account
ncli accounts new -p 1234 -m

# List existing accounts 
ncli accounts list 

# Get account balance 
ncli accounts balance 0x123

# Request some tokens
ncli tokens request 5

# Transfers tokens to other account
ncli tokens transfer 0x123 5

# Publishing a new dataset that can be offered for access
ncli assets publish-dataset --service access --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor \ 
    --contentType text/csv --price 1 --urls https://keyko.io/robots.txt

# Publishing a new dataset that can be offered for compute
ncli assets publish-dataset --service compute --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor \ 
    --contentType text/csv --urls https://keyko.io/robots.txt

# Publishing a new algorithm
ncli assets publish-algorithm --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor --contentType text/text \ 
    --price 0 --language python --entrypoint "python word_count.py" --container python:3.8-alpine \
    --url https://raw.githubusercontent.com/keyko-io/nevermined-sdk-py/examples/word_count.py

# Publishing a new workflow
ncli assets publish-workflow --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor \ 
    --container python:3.8-alpine --inputs did:nv:123,did:nv:456 --transformation did:nv:abc    


# Imports an asset from a JSON file
ncli assets import metadata.json

# Resolves a did
ncli assets resolve did:op:123

# Search for assets
ncli assets search query

# Order
ncli assets order did:op:123

# Get an asset
ncli assets get did:op:123

# Get an asset having already a previous purchased order (via service agreement id) 
ncli assets get did:op:123 -s 0x12321

# Execute a remote compute service
ncli assets exec did:nv:01234

# Get Network Smart Contracts
ncli network list

# Get Smart Contract methods
ncli network describe NeverminedToken

# Call Contract method
ncli network exec NeverminedToken request 1

# Get resource remote information
ncli utils info http://xxx.com/file.zip
```



## License

```
Copyright 2020 Keyko GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
