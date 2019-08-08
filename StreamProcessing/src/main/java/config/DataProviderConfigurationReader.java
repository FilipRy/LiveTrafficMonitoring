package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataProviderConfigurationReader {

    private Properties properties;

    public DataProviderConfigurationReader() {
        this.init();
    }

    private void init() {
        this.properties = new Properties();
        try (InputStream is = TopologyConfigurationReader.class.getClassLoader().getResourceAsStream("provider.properties")) {
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

    public int getDataProviderSubmissionSpeed() {
        return Integer.parseInt((String) properties.get("dataprovider.submissionSpeed"));
    }

    public String getTrafficDataSourcePath() {
        return (String) properties.get("dataprovider.trafficData.path");
    }


}
