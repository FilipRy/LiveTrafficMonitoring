package storm.bolts;

import config.TopologyConfigurationReader;
import model.RoadOccupancy;
import org.apache.log4j.Logger;
import org.apache.storm.state.KeyValueState;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseStatefulBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class TopOccupiedRoadsBolt extends BaseStatefulBolt<KeyValueState<String, PriorityQueue<RoadOccupancy>>> {

    private final static Logger logger = Logger.getLogger(TopOccupiedRoadsBolt.class);

    public static final String TOP_KEY = "top-key";

    private TopologyConfigurationReader topologyConfigurationReader;

    private KeyValueState<String, PriorityQueue<RoadOccupancy>> kvState;
    private OutputCollector collector;

    private int topNOccupiedRoads;

    public TopOccupiedRoadsBolt(int topNOccupiedRoads, TopologyConfigurationReader topologyConfigurationReader) {
        this.topNOccupiedRoads = topNOccupiedRoads;
        this.topologyConfigurationReader = topologyConfigurationReader;
    }

    @Override
    public void initState(KeyValueState<String, PriorityQueue<RoadOccupancy>> state) {
        this.kvState = state;
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        RoadOccupancy roadOccupancy = (RoadOccupancy) input.getValue(0);
        PriorityQueue<RoadOccupancy> mostOccupiedRoadsSorted = this.kvState.get(TOP_KEY);

        if (mostOccupiedRoadsSorted == null) {
            mostOccupiedRoadsSorted = new PriorityQueue<>();
        }

        if (mostOccupiedRoadsSorted.peek() != null) {
            int dayOfMonth = mostOccupiedRoadsSorted.peek().getTimestamp().getDayOfMonth();
            int receivedDayOfMonth = roadOccupancy.getTimestamp().getDayOfMonth();
            if (dayOfMonth != receivedDayOfMonth) {
                mostOccupiedRoadsSorted = new PriorityQueue<>(); // resetting on a new day
            }
        }

        if (mostOccupiedRoadsSorted.size() < this.topNOccupiedRoads) {
            this.addRoadOccEntryToPriorityQueue(mostOccupiedRoadsSorted, roadOccupancy);
            logger.info("Publishing most occupied road data " + mostOccupiedRoadsSorted + " to dashboard road stream");
            this.kvState.put(TOP_KEY, mostOccupiedRoadsSorted);
            collector.emit(this.topologyConfigurationReader.getStormStreamDashboardRoadNotifier(), input, new Values(mostOccupiedRoadsSorted));
        } else {
            RoadOccupancy smallestTopOccupancy = mostOccupiedRoadsSorted.peek();
            if (smallestTopOccupancy.getNumberOfVehiclesPerDay() < roadOccupancy.getNumberOfVehiclesPerDay()) {
                boolean hasReplaced = this.addRoadOccEntryToPriorityQueue(mostOccupiedRoadsSorted, roadOccupancy);
                if (!hasReplaced) {
                    mostOccupiedRoadsSorted.poll();
                }
                logger.info("Publishing most occupied road data " + mostOccupiedRoadsSorted + " to dashboard road stream");
                this.kvState.put(TOP_KEY, mostOccupiedRoadsSorted);
                collector.emit(this.topologyConfigurationReader.getStormStreamDashboardRoadNotifier(), input, new Values(mostOccupiedRoadsSorted));
            }
        }
        collector.ack(input);
    }

    private boolean addRoadOccEntryToPriorityQueue(PriorityQueue<RoadOccupancy> roadOccupancies, RoadOccupancy roadOccupancy) {

        RoadOccupancy foundRoadOccupancy = null;
        Iterator<RoadOccupancy> roadOccupancyIterator = roadOccupancies.iterator();
        while (roadOccupancyIterator.hasNext()) {
            RoadOccupancy readRoadOcc = roadOccupancyIterator.next();
            if (readRoadOcc.getReportId() == roadOccupancy.getReportId()) {
                foundRoadOccupancy = readRoadOcc;
                break;
            }
        }

        if (foundRoadOccupancy != null) {
            roadOccupancies.remove(foundRoadOccupancy);
            roadOccupancies.add(roadOccupancy);
            return true;
        }

        roadOccupancies.add(roadOccupancy);
        return false;
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream(this.topologyConfigurationReader.getStormStreamDashboardRoadNotifier(), new Fields("road-occupancy-data"));
    }
}
