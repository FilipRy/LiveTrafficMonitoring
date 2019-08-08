package dataprovider;

import config.DataProviderConfigurationReader;
import kafka.IKafkaProducer;
import kafka.KafkaProducerBuilder;
import kafka.TrafficDataKafkaProducer;

public class DataProviderExecutor {

    public static void main(String[] args) {

        DataProviderConfigurationReader dataProviderConfigurationReader = new DataProviderConfigurationReader();

        TrafficMetadataInitializer metadataInitializer = new TrafficMetadataInitializer(dataProviderConfigurationReader);
        metadataInitializer.putMetadataToDatabase();

        KafkaProducerBuilder kafkaProducerBuilder = new KafkaProducerBuilder(dataProviderConfigurationReader);
        IKafkaProducer kafkaProducer = new TrafficDataKafkaProducer(kafkaProducerBuilder.createProducer(), dataProviderConfigurationReader);
        TrafficSensorDataProvider provider = new TrafficSensorDataProvider(kafkaProducer, dataProviderConfigurationReader);

        provider.startProvidingData();
    }

}
