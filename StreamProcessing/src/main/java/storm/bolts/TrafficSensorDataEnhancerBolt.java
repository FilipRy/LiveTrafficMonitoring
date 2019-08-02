package storm.bolts;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import config.ConfigurationReader;
import model.TrafficSensorData;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.bson.Document;

import java.util.Map;

public class TrafficSensorDataEnhancerBolt extends BaseRichBolt {

    private final static Logger logger = Logger.getLogger(TrafficSensorDataEnhancerBolt.class);

    private OutputCollector collector;
    private ConfigurationReader configurationReader;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;


    public TrafficSensorDataEnhancerBolt(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.mongoClient = new MongoClient(configurationReader.getMongoDBHost(), configurationReader.getMongoDBPort());
        this.mongoDatabase = this.mongoClient.getDatabase(configurationReader.getMongoDBName());
        this.mongoCollection = this.mongoDatabase.getCollection(this.configurationReader.getMongoDBCollectionName());
    }

    @Override
    public void execute(Tuple input) {

        TrafficSensorData trafficSensorData = (TrafficSensorData) input.getValue(4);

        trafficSensorData = this.enhanceTrafficSensorDataWithLocationMetadata(trafficSensorData);

        logger.info("Publishing TrafficSensorData " + trafficSensorData.toString() + "to dashboard and occupancy streams");

        this.collector.emit(this.configurationReader.getStormStreamDashboardMapNotifier(), input, new Values(trafficSensorData));
        this.collector.emit(this.configurationReader.getStormStreamRoadDailyOccupancy(), input, new Values(trafficSensorData));
        this.collector.ack(input);
    }


    private TrafficSensorData enhanceTrafficSensorDataWithLocationMetadata(TrafficSensorData data) {

        Document document = this.mongoCollection.find(Filters.eq("reportId", data.getReportId())).first();

        double point1Lat = document.getDouble("point1Lat");
        double point1Long = document.getDouble("point1Long");
        double point2Lat = document.getDouble("point2Lat");
        double point2Long = document.getDouble("point2Long");
        String streetName = document.getString("streetName");

        data.setStartPointLatitude(point1Lat);
        data.setStartPointLongitude(point1Long);
        data.setEndPointLatitude(point2Lat);
        data.setEndPointLongitude(point2Long);
        data.setRoadName(streetName);

        return data;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream(this.configurationReader.getStormStreamDashboardMapNotifier(), new Fields("traffic-sensor-data"));
        declarer.declareStream(this.configurationReader.getStormStreamRoadDailyOccupancy(), new Fields("traffic-sensor-data"));
    }
}
