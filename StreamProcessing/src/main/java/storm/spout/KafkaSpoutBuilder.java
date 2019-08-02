package storm.spout;

import config.ConfigurationReader;
import model.TrafficSensorDataDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;

public class KafkaSpoutBuilder {

    private ConfigurationReader configurationReader;

    public KafkaSpoutBuilder(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    public KafkaSpout buildKafkaSpout() {

        KafkaSpoutConfig kafkaSpoutConfig = KafkaSpoutConfig.builder(configurationReader.getKafkaBrokerAddresses(), configurationReader.getKafkaTopic())
                .setProp(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TrafficSensorDataDeserializer.class)
                .setProp(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, TrafficSensorDataDeserializer.class)
                .build();

        KafkaSpout kafkaSpout = new KafkaSpout<>(kafkaSpoutConfig);
        return kafkaSpout;
    }

}
