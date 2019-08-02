package storm.bolts;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigurationReader;
import model.RoadOccupancy;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.PriorityQueue;

public class DashboardRoadOccupancyNotifierBolt extends BaseRichBolt {

    private ConfigurationReader configurationReader;
    private OutputCollector collector;


    public DashboardRoadOccupancyNotifierBolt(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        PriorityQueue<RoadOccupancy> mostOccupiedRoads = (PriorityQueue<RoadOccupancy>) input.getValue(0);

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(mapper);
        restTemplate.getMessageConverters().add(messageConverter);

        RoadOccupancy[] roadOccupancies = new RoadOccupancy[mostOccupiedRoads.size()];
        int i = 0;
        while (!mostOccupiedRoads.isEmpty()) {
            roadOccupancies[i++] = mostOccupiedRoads.poll();
        }

        HttpEntity<RoadOccupancy[]> roadOccupancyHttpEntity = new HttpEntity<>(roadOccupancies);
        restTemplate.postForEntity(configurationReader.getDashboardAddress() + "api/v1/most-occupied-roads", roadOccupancyHttpEntity, RoadOccupancy[].class);

        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
