package dataprovider;

import config.ConfigurationReader;
import kafka.IKafkaProducer;
import kafka.KafkaProducerBuilder;
import kafka.TrafficDataKafkaProducer;

public class DataProviderExecutor {

    public static void main(String[] args) {

        ConfigurationReader configurationReader = new ConfigurationReader();

//        TrafficMetadataInitializer metadataInitializer = new TrafficMetadataInitializer(configurationReader);
//        metadataInitializer.putMetadataToDatabase();

        KafkaProducerBuilder kafkaProducerBuilder = new KafkaProducerBuilder(configurationReader);
        IKafkaProducer kafkaProducer = new TrafficDataKafkaProducer(kafkaProducerBuilder.createProducer(), configurationReader);
        TrafficSensorDataProvider provider = new TrafficSensorDataProvider(kafkaProducer, configurationReader.getDataProviderSubmissionSpeed());

        provider.startProvidingData();
    }

}
