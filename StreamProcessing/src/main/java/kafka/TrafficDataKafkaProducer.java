package kafka;

import config.ConfigurationReader;
import model.TrafficSensorData;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class TrafficDataKafkaProducer implements IKafkaProducer {

    private final static Logger logger = Logger.getLogger(TrafficDataKafkaProducer.class);

    private Producer<Long, TrafficSensorData> producer;
    private ConfigurationReader configurationReader;


    public TrafficDataKafkaProducer(Producer<Long, TrafficSensorData> producer, ConfigurationReader configurationReader) {
        this.producer = producer;
        this.configurationReader = configurationReader;
    }

    @Override
    public void produceTrafficData(Stream<TrafficSensorData> dataStream) {

        dataStream.forEach( data -> {

            ProducerRecord<Long, TrafficSensorData> record = new ProducerRecord<>(configurationReader.getKafkaTopic(), data);

            try {
                RecordMetadata metadata = producer.send(record).get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error when publishing record to kafka " + e);
            }

        } );



    }
}
