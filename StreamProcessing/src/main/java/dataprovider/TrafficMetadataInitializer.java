package dataprovider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import config.DataProviderConfigurationReader;
import org.bson.Document;

import java.io.FileReader;
import java.io.IOException;

public class TrafficMetadataInitializer {

    private DataProviderConfigurationReader providerConfigurationReader;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    public TrafficMetadataInitializer(DataProviderConfigurationReader providerConfigurationReader) {
        this.providerConfigurationReader = providerConfigurationReader;
        this.mongoClient = new MongoClient(providerConfigurationReader.getMongoDBHost(), providerConfigurationReader.getMongoDBPort());
        this.mongoDatabase = this.mongoClient.getDatabase(providerConfigurationReader.getMongoDBName());
        this.mongoCollection = this.mongoDatabase.getCollection(this.providerConfigurationReader.getMongoDBCollectionName());
    }

    public void putMetadataToDatabase() {

        try (FileReader fileReader = new FileReader("src/main/resources/trafficMetaData.csv")) {


            CSVReader csvReader = new CSVReader(fileReader);
            String[] values = null;

            while ((values = csvReader.readNext()) != null) {

                String streetName = values[0] + " " + values[2];
                String point2Longitude = values[5];
                String point1Latitude = values[12];
                String point2Latitude = values[13];
                String point1Longitude = values[19];
                String reportIdStr = values[20];

                double point1Lat = Double.parseDouble(point1Latitude);
                double point1Long = Double.parseDouble(point1Longitude);
                double point2Lat = Double.parseDouble(point2Latitude);
                double point2Long = Double.parseDouble(point2Longitude);
                int reportId = Integer.parseInt(reportIdStr);


                Document doc = new Document("reportId", reportId)
                        .append("streetName", streetName)
                        .append("point1Lat", point1Lat)
                        .append("point1Long", point1Long)
                        .append("point2Lat", point2Lat)
                        .append("point2Long", point2Long);

                this.mongoCollection.insertOne(doc);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

    }

}
