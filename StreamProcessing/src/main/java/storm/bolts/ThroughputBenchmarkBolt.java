package storm.bolts;

import model.ThroughputBenchmark;
import org.apache.log4j.Logger;
import org.apache.storm.state.KeyValueState;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseStatefulBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ThroughputBenchmarkBolt extends BaseStatefulBolt<KeyValueState<Long, ThroughputBenchmark>> {

    private final static Logger logger = Logger.getLogger(ThroughputBenchmarkBolt.class);

    private KeyValueState<Long, ThroughputBenchmark> kvState;
    private OutputCollector outputCollector;

    @Override
    public void initState(KeyValueState<Long, ThroughputBenchmark> state) {
        this.kvState = state;
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.outputCollector = collector;
    }

    @Override
    public void execute(Tuple input) {

        ThroughputBenchmark throughputBenchmark = kvState.get(0l);
        if (throughputBenchmark == null) {
            throughputBenchmark = new ThroughputBenchmark(System.currentTimeMillis(), System.currentTimeMillis(), new AtomicInteger(1));
        }
        else {
            throughputBenchmark.setLastTupleReceivedTimestamp(System.currentTimeMillis());
            throughputBenchmark.getNumberOfTuplesReceived().incrementAndGet();
        }

        int tuplesCount = throughputBenchmark.getNumberOfTuplesReceived().get();
        if (tuplesCount % 1000 == 0) {
            long begin = throughputBenchmark.getFirstTupleReceivedTimestamp() / 1000;
            long end = throughputBenchmark.getLastTupleReceivedTimestamp() / 1000;
            logger.info("Here = " + throughputBenchmark.toString());
            logger.info("Measured throughput by now = " + tuplesCount / (end - begin));
        }

        this.kvState.put(0l, throughputBenchmark);

        this.outputCollector.ack(input);
    }

}
