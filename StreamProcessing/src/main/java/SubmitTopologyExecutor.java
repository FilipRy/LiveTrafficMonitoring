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
        ThroughputBenchmarkBolt throughputBenchmarkBolt = new ThroughputBenchmarkBolt();

        String stormKafkaSpoutId = topologyConfigurationReader.getStormKafkaSpoutId();
        String stormBoltEnhancerId = topologyConfigurationReader.getStormBoltEnhancerId();
        String stormBoltDashboardMapNotifierId = topologyConfigurationReader.getStormBoltDashboardMapNotifierId();
        String stormBoltDashboardRoadNotifierId = topologyConfigurationReader.getStormBoltDashboardRoadNotifierId();
        String stormBoltRoadDailyOccupancyId = topologyConfigurationReader.getStormBoltRoadDailyOccupancyId();
        String stormBoltTopOccupiedRoadId = topologyConfigurationReader.getStormBoltTopOccupiedRoadId();
        String stormBoltBenchmarksId = topologyConfigurationReader.getStormBoltBenchmarkThroughputId();

        String stormStreamDashboardMap = topologyConfigurationReader.getStormStreamDashboardMapNotifier();
        String stormStreamDashboardRoad = topologyConfigurationReader.getStormStreamDashboardRoadNotifier();
        String stormStreamRoadDailyOccupancy = topologyConfigurationReader.getStormStreamRoadDailyOccupancy();
        String stormStreamTopOccupiedRoad = topologyConfigurationReader.getStormStreamTopOccupiedRoads();
        String stormStreamBenchmarks = topologyConfigurationReader.getStormStreamBenchmarks();

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout(stormKafkaSpoutId, kafkaSpout, 4);
        topologyBuilder.setBolt(stormBoltEnhancerId, enhancerBolt, 4).shuffleGrouping(stormKafkaSpoutId);
        topologyBuilder.setBolt(stormBoltRoadDailyOccupancyId, roadDailyOccupancyBolt, 4).shuffleGrouping(stormBoltEnhancerId, stormStreamRoadDailyOccupancy);

        if (topologyConfigurationReader.isBenchmarking()) {
            topologyBuilder.setBolt(stormBoltBenchmarksId, throughputBenchmarkBolt, 1).shuffleGrouping(stormBoltRoadDailyOccupancyId, stormStreamBenchmarks);
        } else {
            topologyBuilder.setBolt(stormBoltDashboardMapNotifierId, dashboardMapNotifierBolt, 4).shuffleGrouping(stormBoltEnhancerId, stormStreamDashboardMap);
            topologyBuilder.setBolt(stormBoltTopOccupiedRoadId, topOccupiedRoadsBolt, 4).shuffleGrouping(stormBoltRoadDailyOccupancyId, stormStreamTopOccupiedRoad);
            topologyBuilder.setBolt(stormBoltDashboardRoadNotifierId, roadOccupancyNotifierBolt, 4).shuffleGrouping(stormBoltTopOccupiedRoadId, stormStreamDashboardRoad);
        }

        Config conf = new Config();
        String topologyName =  topologyConfigurationReader.getStormTopologyName();
        // Defines how many worker processes have to be created for the topology in the cluster.
        conf.setNumWorkers(8);
        conf.setMaxSpoutPending(500);

        StormTopology topology = topologyBuilder.createTopology();
        StormSubmitter.submitTopology(topologyName, conf, topology);
    }
}
