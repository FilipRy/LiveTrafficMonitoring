package storm.bolts;

import config.ConfigurationReader;
import model.RoadOccupancy;
import model.TrafficSensorData;
import org.apache.log4j.Logger;
import org.apache.storm.state.KeyValueState;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseStatefulBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class RoadDailyOccupancyBolt extends BaseStatefulBolt<KeyValueState<Long, RoadOccupancy>> {

    private final static Logger logger = Logger.getLogger(RoadDailyOccupancyBolt.class);

    private KeyValueState<Long, RoadOccupancy> kvState;
    private OutputCollector collector;

    private ConfigurationReader configurationReader;

    public RoadDailyOccupancyBolt(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    @Override
    public void initState(KeyValueState<Long, RoadOccupancy> state) {
        this.kvState = state;
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        TrafficSensorData trafficSensorData = (TrafficSensorData) input.getValue(0);
        logger.debug("Received traffic sensor data in RoadDailyOccupancyBolt: " + trafficSensorData.toString());
        RoadOccupancy roadOccupancy = this.kvState.get(trafficSensorData.getReportId());
        if (roadOccupancy == null) {
            roadOccupancy = new RoadOccupancy(trafficSensorData.getRoadName(), trafficSensorData.getReportId(), trafficSensorData.getTimestamp(), trafficSensorData.getVehicleCount());
            logger.debug("New instance of RoadOccupancy created from sensor data");
        } else {
            if (roadOccupancy.getTimestamp().getDayOfMonth() != trafficSensorData.getTimestamp().getDayOfMonth()) { // sensor data for next day is in tuple
                roadOccupancy.setNumberOfVehiclesPerDay(trafficSensorData.getVehicleCount());
                roadOccupancy.setTimestamp(trafficSensorData.getTimestamp());
                logger.info("RoadOccupancy number of vehicles reset, because its a new day");
            } else {
                int vehiclesOnRoadCount = roadOccupancy.getNumberOfVehiclesPerDay();
                roadOccupancy.setNumberOfVehiclesPerDay(vehiclesOnRoadCount + trafficSensorData.getVehicleCount());
                logger.debug("RoadOccupancy number of vehicles per day updated to " + roadOccupancy.getNumberOfVehiclesPerDay());
            }
        }
        this.kvState.put(roadOccupancy.getReportId(), roadOccupancy);

        logger.info("Publishing RoadOccupancy " + roadOccupancy.toString() + " to top-occupied-roads-stream");

        collector.emit(configurationReader.getStormStreamTopOccupiedRoads(), input, new Values(roadOccupancy));
        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream(configurationReader.getStormStreamTopOccupiedRoads(), new Fields("road-occupancy-data"));
    }

}
