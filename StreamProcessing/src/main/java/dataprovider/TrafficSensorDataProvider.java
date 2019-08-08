package dataprovider;

import com.opencsv.CSVReader;
import config.DataProviderConfigurationReader;
import kafka.IKafkaProducer;
import model.TrafficSensorData;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TrafficSensorDataProvider {

    public static final String START_TIMESTAMP = "2014-02-13T11:30:00";

    private final static Logger logger = Logger.getLogger(TrafficSensorDataProvider.class);

    private IKafkaProducer kafkaProducer;
    private int submissionSpeed;
    private String trafficDataSourcePath;

    public TrafficSensorDataProvider(IKafkaProducer kafkaProducer, DataProviderConfigurationReader dataProviderConfigurationReader) {
        this.kafkaProducer = kafkaProducer;
        this.submissionSpeed = dataProviderConfigurationReader.getDataProviderSubmissionSpeed();
        this.trafficDataSourcePath = dataProviderConfigurationReader.getTrafficDataSourcePath();
    }

    public void startProvidingData() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime currentTime = LocalDateTime.parse(START_TIMESTAMP);

        List<TrafficSensorData> trafficSensorDataList = new ArrayList<>();

        boolean readAllData = false;

        try (FileReader fileReader = new FileReader(this.trafficDataSourcePath)) {


            CSVReader csvReader = new CSVReader(fileReader);
            String[] values = csvReader.readNext();

            while (!readAllData) {
                TrafficSensorData trafficSensorData = this.parseCSVRecordToTrafficData(values);


                while (!trafficSensorData.getTimestamp().isAfter(currentTime)) {
                    trafficSensorDataList.add(trafficSensorData);
                    values = csvReader.readNext();
                    if (values == null) {
                        readAllData = true;
                        break;
                    }
                    trafficSensorData = this.parseCSVRecordToTrafficData(values);
                }

                if (trafficSensorDataList.size() > 0) {
                    logger.info("Current time " + currentTime.format(formatter));
                    logger.info("Publishing " + trafficSensorDataList.size() + " entries to kafka");
                    kafkaProducer.produceTrafficData(trafficSensorDataList.stream());
                }

                trafficSensorDataList.clear();

                currentTime = currentTime.plusSeconds(1);

                Thread.sleep(1000 / this.submissionSpeed);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }

    private TrafficSensorData parseCSVRecordToTrafficData(String[] values) {

        String status = values[0];
        int avgMeasuredTime = Integer.parseInt(values[1]);
        int avgSpeed = Integer.parseInt(values[2]);
        int extID = Integer.parseInt(values[3]);
        int medianMeasuredTime = Integer.parseInt(values[4]);

        LocalDateTime dateTime = LocalDateTime.parse(values[5]);

        int vehicleCount = Integer.parseInt(values[6]);
        long id = Long.parseLong(values[7]);
        int reportId = Integer.parseInt(values[8]);

        TrafficSensorData trafficSensorData = new TrafficSensorData(id, reportId, status, vehicleCount, avgSpeed, dateTime, null, null, null, null, null);

        return trafficSensorData;
    }

}
