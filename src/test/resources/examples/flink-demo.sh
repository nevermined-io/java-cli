#!/usr/bin/env bash

set -e
shopt -s expand_aliases

# Helper functions to capture and parse the relevant information of the output
cap () { tee /tmp/capture.out; }
did () { cat /tmp/capture.out | grep -oP 'did:nv:\w+'; }
sid () { cat /tmp/capture.out | grep -oP 'ServiceAgreementId: \K\w+'; }
eid () { cat /tmp/capture.out | grep -oP '\Knevermined-compute-\w+'; }

alias ncli='java $NEVERMINED_OPTS -jar target/cli-*-shaded.jar'

# clean config
ncli config clean

# create account
ncli accounts new -m -p secret -d /tmp
# request some eth so that we can pay for gas and nvm
ncli tokens request --token eth

# request some tokens from faucet
ncli tokens request --token nvm --nvm 10

# Publish compute to the data asset
ncli assets publish-dataset \
    --service compute \
    --price 1 \
    --title "Nevermined Tools" \
    --author "Keyko" \
    --dateCreated "2020-09-29T12:11:15Z" \
    --contentType text/text \
    --urls "https://raw.githubusercontent.com/nevermined-io/tools/master/README.md" \
    | cap
COMPUTE_DID=$(did)

# Publish algorithm
ncli assets publish-algorithm \
    --title "Word count" \
    --author "Keyko" \
    --dateCreated "2020-09-29T12:11:15Z" \
    --contentType text/text \
    --language "java" \
    --entrypoint 'start-cluster.sh && flink run -c io.keyko.nevermined.examples.WordCountJob ./nevermined-flink-example.jar --input $NEVERMINED_INPUTS_PATH --output $NEVERMINED_OUTPUTS_PATH/result.csv' \
    --container "flink:1.11.2-scala_2.12-java11" \
    --url "https://github.com/nevermined-io/docs/raw/master/resources/nevermined-flink-example.jar" \
    | cap
ALGORITHM_DID=$(did)

# Publish workflow
ncli assets publish-workflow \
    --title "Nevermined Tools" \
    --author "Keyko" \
    --dateCreated "2020-09-29T12:11:15Z" \
    --container "flink:1.11.2-scala_2.12-java11" \
    --inputs $COMPUTE_DID \
    --transformation $ALGORITHM_DID \
    | cap
WORKFLOW_DID=$(did)

# Ordering compute to the data asset
ncli assets order $COMPUTE_DID \
    --serviceIndex 4 \
    | cap
SERVICE_AGREEMENT_ID=$(sid)

# Execute compute service
ncli assets exec $COMPUTE_DID \
    --serviceAgreementId $SERVICE_AGREEMENT_ID \
    --workflow $WORKFLOW_DID \
    | cap
EXECUTION_ID=$(eid)

# Monitor the status of the compute job
ncli compute status \
    --serviceAgreementId $SERVICE_AGREEMENT_ID \
    --executionId $EXECUTION_ID \
    --follow
