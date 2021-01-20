# How to install?

## Installing from source code

The source of the CLI can be downloaded from Github:

```bash
git clone git@github.com:nevermined-io/cli.git
```

The application is written in Java so it requires Java 11 and Maven 3 to compile and run it.
You can compile the application using the following command:

```bash
mvn clean package -DskipTests=true
```

## Using Docker

The CLI is packaged in docker format and available in [Docker Hub](https://hub.docker.com/repository/docker/neverminedio/cli).


## Configuration

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

If you are executing the CLI using a Docker container, the configuration files are also in the same folder inside the docker container: `~/.local/share/nevermined-cli/`.
