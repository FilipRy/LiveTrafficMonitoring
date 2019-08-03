# StreamProcessing
This module implements an Apache Storm topology, composed of a single Kafka spout and five bolts (as illustrated on the figure below). The data provider, which emulates the real traffic sensors by reading the collected real traffic data and publishing them to a kafka broker, is also implemnted within this module.

![topology](https://github.com/FilipRy/LiveTrafficMonitoring/blob/master/StreamProcessing/assets/live_traffic_monitoring.png)

## Prerequisites
1. Installed [Java 8](https://www.java.com/en/download/)
2. Installed [Gradle](https://gradle.org/)
3. Installed [Docker](https://www.docker.com/)

## Configuration
The app has two following configuration files stored in `src/main/resources/topology.properties` (configuration of the storm topology) and `src/main/resources/provider.properties` (configuration of the data provider). You have to override the following fields:
* in `topology.properties`
    * `dashboard.address` has to point to the address of the Dashboard module. If the Dashboard is running at localhost then the `dashboard.address` has to be the ip address of your docker host.
* in `provider.properties`
    * `dataprovider.submissionSpeed`: sets the submission speed for data provider, i.e. if set to 1 then 1 real second = 1 emulated second, if set to 100 then 1 real second = 0.01 emulated second, etc. (Since the traffic data are collected every 5 minutes, we want to have this setting to visualize the changes a bit more frequently.)

## Getting Started

### Preprocessing the traffic data
You can either work with the already preprocessed data ([download]()), or preprocess the data on your own by following the steps below:

1. Download the traffic dataset-1 from [here](http://iot.ee.surrey.ac.uk:8080/datasets.html#traffic).
2. Merge all .csv files into a single one by executing the following script:
```
for FILE in *.csv
do
        exec 5<"$FILE"
        read LINE <&5 
        [ -z "$FIRST" ] && echo "$LINE"
        FIRST="no"

        cat <&5
        exec 5<&-
done > trafficData_all.out
```
3. Rename `trafficData_all.out` to `trafficData_all.csv`
4. Create `import.script` file with the following content:
```
create table trafficData(status text, avgMeasuredTime integer, avgSpeed integer, extId integer, medianMeasuredTime integer, TIMESTAMP datetime, vehicleCount integer, _id integer, REPORT_ID integer);
.mode csv
.import 'trafficData_all.csv' TrafficData
```
5. Execute `sqlite3 TrafficData < import.script`
6. Execute `sqlite3 -csv TrafficData "SELECT * FROM trafficData ORDER BY 6" > trafficData_preprocessed.csv`
### Running
1. Set up a Storm Cluster, composed of a single Nimbus and Supervisor node, kafka broker and mongodb in docker containers.
```
docker-compose up
```
2. Build the topology
```
gradle fatJar
```
3. Submit the built topology to the Storm cluster
```
docker run --link nimbus --network streamprocessing_default -it --rm -v $(pwd)/build/libs/StreamProcessing-all-0.0.1.jar:/topology.jar -v $(pwd)/storm/storm.yaml:/conf/storm.yaml storm:2.0.0 storm jar /topology.jar SubmitTopologyExecutor
```
4. (Optional) Navigate to the Storm UI running at `localhost:8080` to verify that the topology has been submitted.
4. Start the [Dashboard module](https://github.com/FilipRy/LiveTrafficMonitoring/tree/master/Dashboard) so that you can see the visualized data when you start the data provider in the next step.
5. Start the data provider, which emulates the real traffic sensors and publishes the traffic data to kafka broker.
```
gradle executeDataProvider
```
7. Navigate to the Dashboard running at `localhost:9000` in your browser and enjoy!

## Built With

* [Java 8](https://www.java.com/en/download/)
* [Gradle](https://gradle.org/) - Build and Dependency Management
* [Docker](https://www.docker.com/) - Execution Environment
------------------------
Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0).
