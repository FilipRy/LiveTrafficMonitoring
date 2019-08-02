package config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

public class ConfigurationReader implements Serializable {

    private Properties properties;

    public ConfigurationReader() {
        this.init();
    }

    private void init() {
        this.properties = new Properties();
        try (InputStream is = ConfigurationReader.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(is);
        } catch (IOException e) {

        }
    }

    public String getKafkaBrokerAddresses() {
        return (String) properties.get("kafka.brokers.addresses");
    }

    public String getKafkaClientId() {
        return (String) properties.get("kafka.client_id");
    }

    public String getKafkaZookeeper() {
        return (String) properties.get("kafka.zookeeper");
    }

    public String getKafkaTopic() {
        return (String) properties.get("kafka.topic_name");
    }

    public String getMongoDBHost() {
        return (String) properties.get("mongodb.host");
    }

    public int getMongoDBPort() {
        return Integer.parseInt((String) properties.get("mongodb.port"));
    }

    public String getMongoDBName() {
        return (String) properties.get("mongodb.database");
    }

    public String getMongoDBCollectionName() {
        return (String) properties.get("mongodb.collection");
    }

    public String getDashboardAddress() {
        return (String) properties.get("dashboard.address");
    }

    public int getDataProviderSubmissionSpeed() {
        return Integer.parseInt((String) properties.get("dataprovider.submissionSpeed"));
    }

    public String getStormKafkaSpoutId() {
        return (String) properties.get("storm.spout.kafka.id");
    }

    public String getStormBoltEnhancerId() {
        return (String) properties.get("storm.bolt.enhancerBolt.id");
    }

    public String getStormBoltDashboardMapNotifierId() {
        return (String) properties.get("storm.bolt.dashboardNotifier.map.id");
    }

    public String getStormBoltDashboardRoadNotifierId() {
        return (String) properties.get("storm.bolt.dashboardNotifier.road.id");
    }

    public String getStormBoltRoadDailyOccupancyId() {
        return (String) properties.get("storm.bolt.roadDailyOccupancyBolt.id");
    }

    public String getStormBoltTopOccupiedRoadId() {
        return (String) properties.get("storm.bolt.topOccupiedRoadsBolt.id");
    }

    public String getStormStreamDashboardMapNotifier() {
        return (String) properties.get("storm.stream.dashboardNotifier.map");
    }

    public String getStormStreamDashboardRoadNotifier() {
        return (String) properties.get("storm.stream.dashboardNotifier.road");
    }

    public String getStormStreamRoadDailyOccupancy() {
        return (String) properties.get("strom.stream.roadDailyOccupancy");
    }

    public String getStormStreamTopOccupiedRoads() {
        return (String) properties.get("strom.stream.topOccupiedRoads");
    }

    public String getStormTopologyName() {
        return (String) properties.get("storm.topology.name");
    }

}
