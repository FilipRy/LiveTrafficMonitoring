# Live Traffic Monitoring

Live Traffic Monitoring it's a simple app to monitor traffic conditions. The app receives data from emulated traffic sensors placed along the roads in the city. The sensors are counting the number of vehicles, which travel on the roads, in regular time intervals and stream the data to various subscribers. Since I don't have access to any realtime data streams I utilized [the traffic dataset-1](http://iot.ee.surrey.ac.uk:8080/datasets.html#traffic), provided by [CityPulse Dataset Collection](http://iot.ee.surrey.ac.uk:8080/index.html), which contain all real traffic data collected by the real sensors from February till June in 2014 in [Aarhus](https://en.wikipedia.org/wiki/Aarhus). In the [StreamProcessing module](https://github.com/FilipRy/LiveTrafficMonitoring/tree/master/StreamProcessing) I implemented a data provider, which emulates the real traffic sensors and publishes the traffic data to a Kafka broker. Furthermore, an Apache Storm topology, implemented in the StreamProcessing module, consumes the traffic data from the Kafka broker and processes the data. The visualization, implemented within the [Dashboard module](https://github.com/FilipRy/LiveTrafficMonitoring/tree/master/Dashboard), is subsequently notified by the bolts of the Apache Storm topology to depict the traffic data on a map and also show the 5 most occupied roads in the city.

## Modules

### StreamProcessing

This module can be found in the directory `StreamProcessing`.
The module is an implementation of Apache Storm topology, composed of a single kafka spout and five bolts. The topology is implemented in Java.
View the README.md in the project for more information about startup and configuration

### Dashboard

This module can be found in the directory `Dashboard`.
The module is a NodeJS application, which visualizes the processed traffic data on a map.
View the README.md in the project for more information about startup and configuration

![Dashboard](https://github.com/FilipRy/LiveTrafficMonitoring/blob/master/StreamProcessing/assets/dashboard.png)


------------------------
Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0).
