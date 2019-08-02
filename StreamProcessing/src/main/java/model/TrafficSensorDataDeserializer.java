package model;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Map;

public class TrafficSensorDataDeserializer implements Deserializer<TrafficSensorData> {

    private final static Logger logger = Logger.getLogger(TrafficSensorDataDeserializer.class);


    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public TrafficSensorData deserialize(String topic, byte[] data) {

        try (ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(Base64.getDecoder().decode(data))) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayStream)) {
                TrafficSensorData trafficSensorData = (TrafficSensorData) objectInputStream.readObject();
                return trafficSensorData;
            } catch (ClassNotFoundException e) {
                logger.error("Failed to deserialize TrafficSensorData" + e);
                return null;
            }
        } catch (IOException e) {
            logger.error("Failed to deserialize TrafficSensorData" + e);
            return null;
        }
    }

    @Override
    public void close() {

    }
}
