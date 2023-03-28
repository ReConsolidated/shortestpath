package org.example.timeUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Time {
    private int hours;
    private int minutes;
    private int seconds;

    public static Time fromString(String s) {
        String[] parsed = s.split(":");
        int hours = Integer.parseInt(parsed[0]);
        int minutes = Integer.parseInt(parsed[1]);
        int seconds = Integer.parseInt(parsed[2]);
        return new Time(hours, minutes, seconds);
    }

    public double timeTo(Time arrivesAt) {
        return 60 * (60 * (arrivesAt.hours - hours) + (arrivesAt.minutes - minutes)) + arrivesAt.seconds - seconds;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Time(hours, minutes, seconds);
    }

    public boolean isLessThan(Time currentTime) {
        return currentTime.timeTo(this) < 0;
    }

    @Override
    public String toString() {
        return hours + ":" + minutes + ":" + seconds;
    }
}
