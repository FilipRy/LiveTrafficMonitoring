import config.ConfigurationReader;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import storm.bolts.*;
import storm.spout.KafkaSpoutBuilder;

public class SubmitTopologyExecutor {

    public static void main(String[] args) throws Exception {

        ConfigurationReader configurationReader = new ConfigurationReader();

        KafkaSpoutBuilder kafkaSpoutBuilder = new KafkaSpoutBuilder(configurationReader);
        KafkaSpout kafkaSpout = kafkaSpoutBuilder.buildKafkaSpout();

        TrafficSensorDataEnhancerBolt enhancerBolt = new TrafficSensorDataEnhancerBolt(configurationReader);
        DashboardMapNotifierBolt dashboardMapNotifierBolt = new DashboardMapNotifierBolt(configurationReader);
        RoadDailyOccupancyBolt roadDailyOccupancyBolt = new RoadDailyOccupancyBolt(configurationReader);
        TopOccupiedRoadsBolt topOccupiedRoadsBolt = new TopOccupiedRoadsBolt(5, configurationReader);
        DashboardRoadOccupancyNotifierBolt roadOccupancyNotifierBolt = new DashboardRoadOccupancyNotifierBolt(configurationReader);

        String stormKafkaSpoutId = configurationReader.getStormKafkaSpoutId();
        String stormBoltEnhancerId = configurationReader.getStormBoltEnhancerId();
        String stormBoltDashboardMapNotifierId = configurationReader.getStormBoltDashboardMapNotifierId();
        String stormBoltDashboardRoadNotifierId = configurationReader.getStormBoltDashboardRoadNotifierId();
        String stormBoltRoadDailyOccupancyId = configurationReader.getStormBoltRoadDailyOccupancyId();
        String stormBoltTopOccupiedRoadId = configurationReader.getStormBoltTopOccupiedRoadId();

        String stormStreamDashboardMap = configurationReader.getStormStreamDashboardMapNotifier();
        String stormStreamDashboardRoad = configurationReader.getStormStreamDashboardRoadNotifier();
        String stormStreamRoadDailyOccupancy = configurationReader.getStormStreamRoadDailyOccupancy();
        String stormStreamTopOccupiedRoad = configurationReader.getStormStreamTopOccupiedRoads();

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout(stormKafkaSpoutId, kafkaSpout, 1);
        topologyBuilder.setBolt(stormBoltEnhancerId, enhancerBolt, 1).shuffleGrouping(stormKafkaSpoutId);
        topologyBuilder.setBolt(stormBoltDashboardMapNotifierId, dashboardMapNotifierBolt, 1).shuffleGrouping(stormBoltEnhancerId, stormStreamDashboardMap);
        topologyBuilder.setBolt(stormBoltRoadDailyOccupancyId, roadDailyOccupancyBolt, 1).shuffleGrouping(stormBoltEnhancerId, stormStreamRoadDailyOccupancy);
        topologyBuilder.setBolt(stormBoltTopOccupiedRoadId, topOccupiedRoadsBolt, 1).shuffleGrouping(stormBoltRoadDailyOccupancyId, stormStreamTopOccupiedRoad);
        topologyBuilder.setBolt(stormBoltDashboardRoadNotifierId, roadOccupancyNotifierBolt, 1).shuffleGrouping(stormBoltTopOccupiedRoadId, stormStreamDashboardRoad);

        Config conf = new Config();
        String topologyName =  configurationReader.getStormTopologyName();
        // Defines how many worker processes have to be created for the topology in the cluster.
        conf.setNumWorkers(5);
        conf.setMaxSpoutPending(5000);

        StormTopology topology = topologyBuilder.createTopology();
        StormSubmitter.submitTopology(topologyName, conf, topology);
    }
}
