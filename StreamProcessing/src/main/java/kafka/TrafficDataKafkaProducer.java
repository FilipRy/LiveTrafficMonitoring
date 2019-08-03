package kafka;

import config.DataProviderConfigurationReader;
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
    private DataProviderConfigurationReader dataProviderConfigurationReader;


    public TrafficDataKafkaProducer(Producer<Long, TrafficSensorData> producer, DataProviderConfigurationReader providerConfigurationReader) {
        this.producer = producer;
        this.dataProviderConfigurationReader = providerConfigurationReader;
    }

    @Override
    public void produceTrafficData(Stream<TrafficSensorData> dataStream) {

        dataStream.forEach( data -> {

            ProducerRecord<Long, TrafficSensorData> record = new ProducerRecord<>(dataProviderConfigurationReader.getKafkaTopic(), data);

            try {
                RecordMetadata metadata = producer.send(record).get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error when publishing record to kafka " + e);
            }

        } );



    }
}
