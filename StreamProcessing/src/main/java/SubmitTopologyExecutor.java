import config.TopologyConfigurationReader;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import storm.bolts.*;
import storm.spout.KafkaSpoutBuilder;

public class SubmitTopologyExecutor {

    public static void main(String[] args) throws Exception {

        TopologyConfigurationReader topologyConfigurationReader = new TopologyConfigurationReader();

        KafkaSpoutBuilder kafkaSpoutBuilder = new KafkaSpoutBuilder(topologyConfigurationReader);
        KafkaSpout kafkaSpout = kafkaSpoutBuilder.buildKafkaSpout();

        TrafficSensorDataEnhancerBolt enhancerBolt = new TrafficSensorDataEnhancerBolt(topologyConfigurationReader);
        DashboardMapNotifierBolt dashboardMapNotifierBolt = new DashboardMapNotifierBolt(topologyConfigurationReader);
        RoadDailyOccupancyBolt roadDailyOccupancyBolt = new RoadDailyOccupancyBolt(topologyConfigurationReader);
        TopOccupiedRoadsBolt topOccupiedRoadsBolt = new TopOccupiedRoadsBolt(5, topologyConfigurationReader);
        DashboardRoadOccupancyNotifierBolt roadOccupancyNotifierBolt = new DashboardRoadOccupancyNotifierBolt(topologyConfigurationReader);

        String stormKafkaSpoutId = topologyConfigurationReader.getStormKafkaSpoutId();
        String stormBoltEnhancerId = topologyConfigurationReader.getStormBoltEnhancerId();
        String stormBoltDashboardMapNotifierId = topologyConfigurationReader.getStormBoltDashboardMapNotifierId();
        String stormBoltDashboardRoadNotifierId = topologyConfigurationReader.getStormBoltDashboardRoadNotifierId();
        String stormBoltRoadDailyOccupancyId = topologyConfigurationReader.getStormBoltRoadDailyOccupancyId();
        String stormBoltTopOccupiedRoadId = topologyConfigurationReader.getStormBoltTopOccupiedRoadId();

        String stormStreamDashboardMap = topologyConfigurationReader.getStormStreamDashboardMapNotifier();
        String stormStreamDashboardRoad = topologyConfigurationReader.getStormStreamDashboardRoadNotifier();
        String stormStreamRoadDailyOccupancy = topologyConfigurationReader.getStormStreamRoadDailyOccupancy();
        String stormStreamTopOccupiedRoad = topologyConfigurationReader.getStormStreamTopOccupiedRoads();

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout(stormKafkaSpoutId, kafkaSpout, 1);
        topologyBuilder.setBolt(stormBoltEnhancerId, enhancerBolt, 1).shuffleGrouping(stormKafkaSpoutId);
        topologyBuilder.setBolt(stormBoltDashboardMapNotifierId, dashboardMapNotifierBolt, 1).shuffleGrouping(stormBoltEnhancerId, stormStreamDashboardMap);
        topologyBuilder.setBolt(stormBoltRoadDailyOccupancyId, roadDailyOccupancyBolt, 1).shuffleGrouping(stormBoltEnhancerId, stormStreamRoadDailyOccupancy);
        topologyBuilder.setBolt(stormBoltTopOccupiedRoadId, topOccupiedRoadsBolt, 1).shuffleGrouping(stormBoltRoadDailyOccupancyId, stormStreamTopOccupiedRoad);
        topologyBuilder.setBolt(stormBoltDashboardRoadNotifierId, roadOccupancyNotifierBolt, 1).shuffleGrouping(stormBoltTopOccupiedRoadId, stormStreamDashboardRoad);

        Config conf = new Config();
        String topologyName =  topologyConfigurationReader.getStormTopologyName();
        // Defines how many worker processes have to be created for the topology in the cluster.
        conf.setNumWorkers(4);
        conf.setMaxSpoutPending(5000);

        StormTopology topology = topologyBuilder.createTopology();
        StormSubmitter.submitTopology(topologyName, conf, topology);
    }
}
