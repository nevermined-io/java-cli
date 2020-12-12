#!/usr/bin/env bash

FILE_CONFIG=$1
NETWORK=${2:-"spree"}
ARTIFACTS=${3:-"$HOME/.nevermined/nevermined-contracts/artifacts"}

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
    "NeverminedToken"
    "SignCondition"
    "TemplateStoreManager"
    "ThresholdCondition"
    "WhitelistingCondition"
    "EscrowAccessSecretStoreTemplate"
    "EscrowComputeExecutionTemplate"
)

until [ $COMMAND_STATUS -eq 0 ] || [ $RETRY_COUNT -eq 240 ]; do
  cat $ARTIFACTS/ready
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
   address=$(jq -r .address "${ARTIFACTS}/$c.$NETWORK.json")
   echo "Setting up $c address to $address"
   sed -i  "s/contract.$c.address=.*/contract.$c.address=\"$address\"/g" $FILE_CONFIG

done

