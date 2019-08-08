package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@AllArgsConstructor
public class ThroughputBenchmark implements Serializable {

    @Getter
    @Setter
    private long firstTupleReceivedTimestamp;

    @Getter
    @Setter
    private long lastTupleReceivedTimestamp;

    @Getter
    @Setter
    private AtomicInteger numberOfTuplesReceived;


    @Override
    public String toString() {
        return "ThroughputBenchmark{" +
                "firstTupleReceivedTimestamp=" + firstTupleReceivedTimestamp +
                ", lastTupleReceivedTimestamp=" + lastTupleReceivedTimestamp +
                ", numberOfTuplesReceived=" + numberOfTuplesReceived +
                '}';
    }
}
