package storm.bolts;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.TopologyConfigurationReader;
import model.TrafficSensorData;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class DashboardMapNotifierBolt extends BaseRichBolt {

    private TopologyConfigurationReader topologyConfigurationReader;
    private OutputCollector collector;

    public DashboardMapNotifierBolt(TopologyConfigurationReader topologyConfigurationReader) {
        this.topologyConfigurationReader = topologyConfigurationReader;
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
        restTemplate.postForObject(topologyConfigurationReader.getDashboardAddress() + "/api/v1/traffic-report", trafficSensorDataHttpEntity, TrafficSensorData.class);
        this.collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
