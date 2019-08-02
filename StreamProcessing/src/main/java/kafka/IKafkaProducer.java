package kafka;

import model.TrafficSensorData;

import java.util.stream.Stream;

public interface IKafkaProducer {

    void produceTrafficData(Stream<TrafficSensorData> data);

}
