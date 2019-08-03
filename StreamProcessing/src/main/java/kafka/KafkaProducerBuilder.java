package kafka;

import config.DataProviderConfigurationReader;
import config.TopologyConfigurationReader;
import model.TrafficSensorData;
import model.TrafficSensorDataSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.Properties;

public class KafkaProducerBuilder {

    private DataProviderConfigurationReader providerConfigurationReader;
    
    public KafkaProducerBuilder(DataProviderConfigurationReader providerConfigurationReader) {
        this.providerConfigurationReader = providerConfigurationReader;
    }

    public Producer<Long, TrafficSensorData> createProducer() {

        String kafkaBrokers = providerConfigurationReader.getKafkaBrokerAddresses();
        String clientId = providerConfigurationReader.getKafkaClientId();

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TrafficSensorDataSerializer.class.getName());
        //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
        return new KafkaProducer<>(props);
    }

}
