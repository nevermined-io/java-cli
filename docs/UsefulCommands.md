# Useful Commands

Here a list of useful commands to interact with a Nevermined network using the CLI:

## Configuration

### Show version information
```bash
ncli --version
```

### Show config information
```bash
ncli config show
```

### Clean previous configuration parameters
```bash
ncli config clean
```

### Show help
```bash
ncli --help
```

## Accounts management

### Create a new account
```bash
ncli accounts new -m --password
```

### List existing accounts
```bash
ncli accounts list
```

### Get account balance
```bash
ncli accounts balance --address 0x123
```

### Import the credentials from a mnemonic
```bash
ncli accounts import --mnemonic "car house green ..." --index 0 --password
```

## Tokens

### Request some tokens
```bash
ncli tokens request
```

### Transfers tokens to other account
```bash
ncli tokens transfer 0x123 5
```

## Assets

### Publishing a new dataset that can be offered for access
```bash
ncli assets publish-dataset --service access --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor \
    --contentType text/csv --price 1 --urls https://github.com/robots.txt
```

### Publishing a new dataset that can be offered for compute
```bash
ncli assets publish-dataset --service compute --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor \
    --contentType text/csv --urls https://github.com/robots.txt    
```

### Publishing a new algorithm
```bash
ncli assets publish-algorithm --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor --contentType text/text \
    --price 0 --language python --entrypoint "python word_count.py" --container python:3.8-alpine \
    --url https://raw.githubusercontent.com/nevermined-io/sdk-py/examples/word_count.py
```

### Publishing a new workflow
```bash
ncli assets publish-workflow --title "test" --dateCreated "2019-10-10T17:00:000Z" --author aitor \
    --container python:3.8-alpine --inputs did:nv:123,did:nv:456 --transformation did:nv:abc    
```

### Imports an asset from a JSON file
```bash
ncli assets import metadata.json
```

### Resolves a did
```bash
ncli assets resolve did:nv:123
```

### Search for assets
```bash
ncli assets search query
```

### Order
```bash
ncli assets order did:nv:123
```

### Get an asset
```bash
ncli assets get did:nv:123
```

### Get an asset having already a previous purchased order (via service agreement id)
```bash
ncli assets get did:nv:123 -s 0x12321
```

### Execute a remote compute service
```bash
ncli assets exec did:nv:01234
```


## Network commands

### Get Network Smart Contracts
```bash
ncli network list
```

### Get Smart Contract methods
```bash
ncli network describe NeverminedToken
```

### Call Contract method
```bash
ncli network exec NeverminedToken request 1
```

## Dealing with NFTs

### Mint 10 NFTs associated to a DID
```bash
ncli nft mint did:nv:aabb 10
```

### Burn 5 NFTs associated to a DID
```bash
ncli nft burn did:nv:aabb 5
```

### Get NFT balance for an account and DID
```bash
ncli nft balance did:nv:aabb 0x1234
```

### Transfer 4 NFTs from one DID to a different address
```bash
ncli nft transfer did:nv:aabb 0x5678 4
```


## Provenance Methods

### Register Provenance asset usage
```bash
ncli provenance used did:nv:123 --provenanceId 1234 --agent 0xabcd --activity 0423 --signature 0x49324 --attributes access
```

### Register Provenance asset derivation
```bash
ncli provenance derivation --newAsset did:nv:123 --derivedFrom did:nv:456 --provenanceId 1234 --agent 0xabcd --activity 0534 --attributes copy
```

### Register Provenance asset association
```bash
ncli provenance association did:nv:123 --provenanceId 1234 --agent 0xabcd --activity 0534 --attributes "gave access to xxx"
```

### Register Provenance asset delegation
```bash
ncli provenance delegation did:nv:123 --provenanceId 1234 --delegatedAgent 0xabcd --activity 0534 --signature 0x0123 --attributes "was delegated to yyy"
```

### Get Provenance entry details given a provenance id
```bash
ncli provenance inspect 1234
```

### Get Provenance history about a DID
```bash
ncli provenance history did:nv:1234
```

### Check if an address is a provenance delegate
```bash
ncli provenance is-delegate 0xb0b did:nv:1234
```

### Add an address as a delegate of a did
```bash
ncli provenance add-delegate 0xb0b did:nv:1234
```

### Remove an address as a delegate of a did
```bash
ncli provenance remove-delegate  0xb0b did:nv:1234
```


## Others

### Get resource remote information
```bash
ncli utils info http://xxx.com/file.zip
```
