#!/bin/bash

FILE_CONFIG=$1
NETWORK=${2:-"spree"}

RETRY_COUNT=0
COMMAND_STATUS=1

declare -a contracts=(
    "AccessSecretStoreCondition"
    "AgreementStoreManager"
    "ComputeExecutionCondition"
    "ConditionStoreManager"
    "DIDRegistry"
    "DIDRegistryLibrary"
    "Dispenser"
    "EpochLibrary"
    "EscrowReward"
    "HashLockCondition"
    "LockRewardCondition"
    "OceanToken"
    "SignCondition"
    "TemplateStoreManager"
    "ThresholdCondition"
    "WhitelistingCondition"
    "EscrowAccessSecretStoreTemplate"
    "EscrowComputeExecutionTemplate"
)

until [ $COMMAND_STATUS -eq 0 ] || [ $RETRY_COUNT -eq 240 ]; do
  cat ~/.nevermined/nevermined-contracts/artifacts/ready
  COMMAND_STATUS=$?
  if [ $COMMAND_STATUS -eq 0 ]; then
    break
  fi
  sleep 3
  let RETRY_COUNT=RETRY_COUNT+1
done

if [ $COMMAND_STATUS -ne 0 ]; then
  echo "Waited for more than two minutes, but contracts have not been migrated yet. Did you run an Ethereum RPC client and the migration script?"
  exit 1
fi

for c in "${contracts[@]}"
do
   address=$(jq -r .address "${HOME}/.nevermined/nevermined-contracts/artifacts/$c.$NETWORK.json")
   echo "Setting up $c address to $address"
   sed -i  "s/contract.$c.address=.*/contract.$c.address=\"$address\"/g" $FILE_CONFIG

done

