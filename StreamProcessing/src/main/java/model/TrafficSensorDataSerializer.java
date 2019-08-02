package model;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Map;

public class TrafficSensorDataSerializer implements Serializer<TrafficSensorData> {

    private final static Logger logger = Logger.getLogger(TrafficSensorDataSerializer.class);

    private boolean isKey = true;


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.isKey = isKey;
    }

    @Override
    public byte[] serialize(String topic, TrafficSensorData data) {

        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream)) {
                objectOutputStream.writeObject(data);
            }
            return Base64.getEncoder().encode(byteOutputStream.toByteArray());
        } catch (IOException e) {
            logger.error("Error when serializing object: " + e);
            return null;
        }

    }

    @Override
    public void close() {

    }
}
