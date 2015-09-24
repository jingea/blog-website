title: Mycat
---

## schema.xml
### schema>
* `dataNode` : 
* `checkSQLschema` : 
* `sqlMaxLimit` : 
* `name` : 
### table
* `name` : 
* `rule` : 
* `ruleRequired` : 
* `primaryKey` : 
* `type` : 
* `autoIncrement` : 
* `needAddLimit` : 
### childTable
* `name` : 
* `joinKey` : 
* `parentKey` : 
* `primaryKey` : 
* `needAddLimit` : 
### dataNode
* `name` : 
#### dataHost
* `name` : 
* `maxCon` : 
* `minCon` : 
* `balance` : 
* `writeType` : 
* `dbType` : 
* `dbDriver` : 
* `heartbeat` : 
* `writeHost` : 
* `host` : 
* `url` : 
* `password` : 
* `user` : 
* `readHost` : 
* `host` : 
* `url` : 
* `password` : 
* `user` : 
* `database` : 
## server
### user
* `name` : 
* `property` : 
* `name` : 
### system
#### property
* `defaultSqlParser` : 
* `processors` : 
* `processorBufferChunk` : 
* `processorBufferPool` : 
* `processorBufferLocalPercent` : 
* `processorExecutor` : 
* `sequnceHandlerType` : 
* `frontSocketSoRcvbuf` : 
* `frontSocketSoSndbuf` : 
* `frontSocketNoDelay` : 
* `packetHeaderSize` : 
* `maxPacketSize` : 
* `idleTimeout` : 
* `charset` : 
* `txIsolation` : 
* `sqlExecuteTimeout` : 
* `processorCheckPeriod` : 
* `dataNodeIdleCheckPeriod` : 
* `dataNodeHeartbeatPeriod` : 
* `bindIp` : 
* `serverPort` : 
* `managerPort` : 
## rule
### tableRule
* `name` : 
#### rule
##### columns
##### algorithm
### function
* `name` : 
* `class` : 
#### property>
* `name` : 