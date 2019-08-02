package storm.bolts;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigurationReader;
import model.TrafficSensorData;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class DashboardMapNotifierBolt extends BaseRichBolt {

    private ConfigurationReader configurationReader;
    private OutputCollector collector;

    public DashboardMapNotifierBolt(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        TrafficSensorData trafficSensorData = (TrafficSensorData) input.getValue(0);
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(mapper);
        restTemplate.getMessageConverters().add(messageConverter);
        HttpEntity<TrafficSensorData> trafficSensorDataHttpEntity = new HttpEntity<>(trafficSensorData);
        restTemplate.postForObject(configurationReader.getDashboardAddress() + "/api/v1/traffic-report", trafficSensorDataHttpEntity, TrafficSensorData.class);
        this.collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
