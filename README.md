**Scala RESTful API for shipping process dedicated to CQRS pattern experiences**  

This repository is part of an experimental project on Event Sourcing architecture implementing the CQRS pattern.

**It include:** <br/>
->Apache Kafka: Messaging agent service. Consumer functions used only.<br/>
<br/>
**Functions included:** <br/>
->Create, delete an order.<br/>
->Update status of an order to next step (if possible).<br/>
->Replay every event of an order<br/>
->Synchronize Order object between database & event store