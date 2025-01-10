# Hyperledger fabric
adapted to run election chaincode.

### How to setup:
- clone this repository
- run the fabric sample script like so:
```bash
./install-fabric.sh --fabric-version 2.5.9 --ca-version 1.5.12 binaries
```
- possibly do this:
```bash
./install-fabric.sh --fabric-version 2.5.9 --ca-version 1.5.12 binaries docker
```

### How to run:
- the project is meant to be ran with [the capstone voting system repository](https://github.com/ArthurAtangana/capstone-voting-system)
- to run the network independently, in the test-network directory:
```bash
./network.sh down
./network.sh up createChannel -c mychannel -ca
./network.sh deployCC -ccn basic -ccp ../asset-transfer/basic/chaincode-java -ccl java
```

