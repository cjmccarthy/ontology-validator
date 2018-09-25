##Ontology Server

A server for validating objects against a provided ontology

##Installation

The project is built using maven. In order to build it, one must install maven and set it up

Instructions for maven can be found here: https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

To deploy the project locally, this is all that is needed. The command "mvn package" will build the jar with all needed depenencies.

##Docker

In order to build the project to a docker container, one must also have docker (or boot2docker for osx installed on mac)

Instructions for docker can be found here under the "Install" tab: https://docs.docker.com/

In order to build and install the latest build to a docker container:
  
    mvn clean package docker:build

In order to run the container:

    docker run -d -p 8888:8888 ontology-server

##Benchmarking

A benchmarking project is included, under benchmark/

In order to run the benchmark, the machine must have node.js and grunt installed

Detailed Instructions on the benchmarking tool can be found here: https://github.com/matteofigus/grunt-api-benchmark

The config file is already included at benchmark/node_modules/grunt-api-benchmark/test/fixtures/input1.json. The IP address and port may need to be changed depending on your configuration. Data payloads can be changed there as well, if desired

Once changed, the benchmark can be run from anywhere in the benchmark/node_modules/grunt-api-benchmark/ directory by running:

    grunt benchmark

Output will be located at benchmark/node_modules/grunt-api-benchmark/generated/output1.html





