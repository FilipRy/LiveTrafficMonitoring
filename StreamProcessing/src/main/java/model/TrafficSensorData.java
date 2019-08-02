package model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public class TrafficSensorData implements Serializable {


    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private long reportId;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private int vehicleCount;

    @Getter
    @Setter
    private int avgSpeed;

    @Getter
    @Setter
    private LocalDateTime timestamp;

    @Getter
    @Setter
    private String roadName;

    @Getter
    @Setter
    private Double startPointLatitude;

    @Getter
    @Setter
    private Double startPointLongitude;

    @Getter
    @Setter
    private Double endPointLatitude;

    @Getter
    @Setter
    private Double endPointLongitude;


    @Override
    public String toString() {
        return "TrafficSensorData{" +
                "id=" + id +
                ", reportId=" + reportId +
                ", status='" + status + '\'' +
                ", vehicleCount=" + vehicleCount +
                ", avgSpeed=" + avgSpeed +
                ", timestamp=" + timestamp +
                ", roadName='" + roadName + '\'' +
                ", startPointLatitude=" + startPointLatitude +
                ", startPointLongitude=" + startPointLongitude +
                ", endPointLatitude=" + endPointLatitude +
                ", endPointLongitude=" + endPointLongitude +
                '}';
    }
}
