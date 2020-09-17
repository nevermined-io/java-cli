#!/usr/bin/env bash

env

ROOT_PATH="/"

mkdir -p /root/.local/share/nevermined-cli/networks/
envsubst < $ROOT_PATH/src/main/resources/application.template > /root/.local/share/nevermined-cli/application.conf
envsubst < $ROOT_PATH/src/main/resources/log4j2.template > /root/.local/share/nevermined-cli/log4j2.properties
envsubst < $ROOT_PATH/src/main/resources/networks/networks.template > /root/.local/share/nevermined-cli/networks/${KEEPER_NETWORK_NAME}.conf

$ROOT_PATH/src/main/bash/updateConfAddresses.sh /root/.local/share/nevermined-cli/networks/${KEEPER_NETWORK_NAME}.conf ${KEEPER_NETWORK_NAME}

tail -f /dev/null
