package storm.spout;

import config.TopologyConfigurationReader;
import model.TrafficSensorDataDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;

public class KafkaSpoutBuilder {

    private TopologyConfigurationReader topologyConfigurationReader;

    public KafkaSpoutBuilder(TopologyConfigurationReader topologyConfigurationReader) {
        this.topologyConfigurationReader = topologyConfigurationReader;
    }

    public KafkaSpout buildKafkaSpout() {

        KafkaSpoutConfig kafkaSpoutConfig = KafkaSpoutConfig.builder(topologyConfigurationReader.getKafkaBrokerAddresses(), topologyConfigurationReader.getKafkaTopic())
                .setProp(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TrafficSensorDataDeserializer.class)
                .setProp(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, TrafficSensorDataDeserializer.class)
                .build();

        KafkaSpout kafkaSpout = new KafkaSpout<>(kafkaSpoutConfig);
        return kafkaSpout;
    }

}
