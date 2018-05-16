# State Calculation

A small and simplified piece of StackState's logic pertaining to the calculation of states between components.
It visualizes a dependency graph of interconnected components in Neo4j. Each component can be connected to multiple other components. Each relation represents a dependency. The target of each relation is a component that the other component depends upon.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
* [Install Neo4j](https://neo4j.com/docs/operations-manual/current/installation/)
* Create a new database. See example [here](https://neo4j.com/developer/example-project/)

## Running the tests

Configure database properties in resource file *neo4jParameters.properties*

Then run :

`java -jar target/scala-2.12/sts-state-challenge-assembly-0.1.jar -i path/initial.json -e path events.json`


## Built with
* Sbt
* Neo4j-java-driver 1.6.1
* Json4s-jackson 3.5.0
