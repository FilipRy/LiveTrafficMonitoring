package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public class RoadOccupancy implements Comparable<RoadOccupancy>, Serializable {


    @Getter
    @Setter
    private String roadName;

    @Getter
    @Setter
    private long reportId;

    @Getter
    @Setter
    private LocalDateTime timestamp;

    @Getter
    @Setter
    private int numberOfVehiclesPerDay;

    @Override
    public int compareTo(RoadOccupancy o) {
        return this.numberOfVehiclesPerDay - o.getNumberOfVehiclesPerDay();
    }

    @Override
    public String toString() {
        return "RoadOccupancy{" +
                "roadName='" + roadName + '\'' +
                ", reportId=" + reportId +
                ", timestamp=" + timestamp +
                ", numberOfVehiclesPerDay=" + numberOfVehiclesPerDay +
                '}';
    }
}
