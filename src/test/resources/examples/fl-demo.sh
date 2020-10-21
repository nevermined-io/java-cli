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
# request some eth so that we can pay for gas
ncli tokens request --eth

# request some tokens from faucet
ncli tokens request --tokens 10

# [DATA_PROVIDER0 --> NEVERMINED] Publishing compute to the data asset for asset0
ncli assets publish-dataset \
    --service compute \
    --price 1 \
    --title "Data Part 0" \
    --author "Keyko" \
    --dateCreated "2020-09-29T12:11:15Z" \
    --contentType text/text \
    --urls "https://raw.githubusercontent.com/keyko-io/nevermined-docs/2c3fdfc9c559daaf1097a6a88a8bee8ce06382c4/resources/data_part_0.csv" \
    | cap
COMPUTE0_DID=$(did)

# [DATA_PROVIDER1 --> NEVERMINED] Publishing compute to the data asset for asset1
ncli assets publish-dataset \
    --service compute \
    --price 1 \
    --title "Data Part 1" \
    --author "Keyko" \
    --dateCreated "2020-09-29T12:11:15Z" \
    --contentType text/text \
    --urls "https://raw.githubusercontent.com/keyko-io/nevermined-docs/2c3fdfc9c559daaf1097a6a88a8bee8ce06382c4/resources/data_part_1.csv" \
    | cap
COMPUTE1_DID=$(did)

# [COORDINATOR_PROVIDER --> NEVERMINED] Publishing coordinator compute asset
ncli assets publish-dataset \
    --service compute \
    --price 1 \
    --title "Coordinator" \
    --author "Keyko" \
    --dateCreated "2020-09-29T12:11:15Z" \
    --contentType text/text \
    --urls "" \
    | cap
COMPUTE_COORDINATOR_DID=$(did)

# [DATA_SCIENTIST --> NEVERMINED] Publishing algorithm asset
ncli assets publish-algorithm \
    --title "FL Participant" \
    --author "Keyko" \
    --dateCreated "2020-09-29T12:11:15Z" \
    --contentType text/text \
    --language "python" \
    --entrypoint 'run-participant --data-directory data --coordinator-url http://172.17.0.2:8081 --write-performance-metrics $NEVERMINED_OUTPUTS_PATH/perf.txt' \
    --container "keykoio/xain-fl-participant:latest" \
    --url "https://raw.githubusercontent.com/keyko-io/nevermined-tools/master/README.md" \
    | cap
ALGORITHM_DID=$(did)

# [DATA_SCIENTIST --> NEVERMINED] Publishing compute workflow for asset0
ncli assets publish-workflow \
    --title "Nevermined Tools" \
    --author "Keyko" \
    --dateCreated "2020-09-29T12:11:15Z" \
    --container "keykoio/xain-fl-participant:latest" \
    --inputs $COMPUTE0_DID \
    --transformation $ALGORITHM_DID \
    | cap
WORKFLOW0_DID=$(did)

# [DATA_SCIENTIST --> NEVERMINED] Publishing compute workflow for asset1
ncli assets publish-workflow \
    --title "Nevermined Tools" \
    --author "Keyko" \
    --dateCreated "2020-09-29T12:11:15Z" \
    --container "keykoio/xain-fl-participant:latest" \
    --inputs $COMPUTE1_DID \
    --transformation $ALGORITHM_DID \
    | cap
WORKFLOW1_DID=$(did)

# [DATA_SCIENTIST --> NEVERMINED] Publishing compute workflow for coordinator
ncli assets import \
    src/test/resources/metadata/metadata_workflow_coordinator.json \
    | cap
WORKFLOW_COORDINATOR_DID=$(did)

# [DATA_SCIENTIST --> DATA_PROVIDER0] Requesting an agreement for compute to the data for asset0
ncli assets order $COMPUTE0_DID \
    --serviceIndex 4 \
    | cap
SERVICE_AGREEMENT0_ID=$(sid)

# [DATA_SCIENTIST --> DATA_PROVIDER0] Requesting an agreement for compute to the data for asset1
ncli assets order $COMPUTE1_DID \
    --serviceIndex 4 \
    | cap
SERVICE_AGREEMENT1_ID=$(sid)

# [DATA_SCIENTIST --> COORDINATOR_PROVIDER] Requesting an agreement for coordinator compute
ncli assets order $COMPUTE_COORDINATOR_DID \
    --serviceIndex 4 \
    | cap
SERVICE_AGREEMENT_COORDINATOR_ID=$(sid)

# [DATA_SCIENTIST --> COORDINATOR_PROVIDER] Requesting execution for coordinator compute
ncli assets exec $COMPUTE_COORDINATOR_DID \
    --serviceAgreementId $SERVICE_AGREEMENT_COORDINATOR_ID \
    --workflow $WORKFLOW_COORDINATOR_DID \
    | cap
EXECUTION_COORDINATOR_ID=$(eid)

# [DATA_SCIENTIST --> DATA_PROVIDER0] Requesting execution for compute to data for asset0
ncli assets exec $COMPUTE0_DID \
    --serviceAgreementId $SERVICE_AGREEMENT0_ID \
    --workflow $WORKFLOW0_DID \
    | cap
EXECUTION0_ID=$(eid)

# [DATA_SCIENTIST --> DATA_PROVIDER1] Requesting execution for compute to data for asset1
ncli assets exec $COMPUTE1_DID \
    --serviceAgreementId $SERVICE_AGREEMENT1_ID \
    --workflow $WORKFLOW1_DID \
    | cap
EXECUTION1_ID=$(eid)

# Monitor the status of the coordinator compute job
ncli compute status \
    --serviceAgreementId $SERVICE_AGREEMENT_COORDINATOR_ID \
    --executionId $EXECUTION_COORDINATOR_ID \
    --follow | cap
COORDINATOR_OUTPUT_DID=$(did)

# Monitor the status of participant 0
ncli compute status \
    --serviceAgreementId $SERVICE_AGREEMENT0_ID \
    --executionId $EXECUTION0_ID \
    --follow | cap
EXECUTION0_OUTPUT_DID=$(did)

# Monitor the status of participant 1
ncli compute status \
    --serviceAgreementId $SERVICE_AGREEMENT1_ID \
    --executionId $EXECUTION1_ID \
    --follow | cap
EXECUTION1_OUTPUT_DID=$(did)

# Download the outputs
ncli assets download-my-asset $COORDINATOR_OUTPUT_DID
ncli assets download-my-asset $EXECUTION0_OUTPUT_DID
ncli assets download-my-asset $EXECUTION1_OUTPUT_DID
