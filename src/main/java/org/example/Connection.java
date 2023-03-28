package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.timeUtils.Time;

@Getter
@AllArgsConstructor
public class Connection {
    private long id;
    private String line;
    private Stop from;
    private Stop to;
    private Time departsAt;
    private Time arrivesAt;

    public double getTime() {
        return departsAt.timeTo(arrivesAt);
    }

    public Double getWeight(Time currentTime) {

        double e = currentTime.timeTo(arrivesAt);
        if (e < 0) {
            e += 60*60*24;
        }
        return e;
    }

    @Override
    public String toString() {
        return "(" +
                "line '" + line + '\'' +
                ", departsAt=" + departsAt +
                ", arrivesAt=" + arrivesAt +
                ')';
    }
}
