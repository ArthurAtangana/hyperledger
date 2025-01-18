#!/bin/bash

# navigate up to the proper directory
cd test-network

# bring any potential network down
./network.sh down
# start the network with a channel (rename)
./network.sh up createChannel -c mychannel -ca
# deploy the chaincode (smart contract)
./network.sh deployCC -ccn basic -ccp ../asset-transfer-basic/chaincode-java -ccl java

