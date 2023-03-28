package org.example;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Stop  {
    private final String name;
    private final double longitude;
    private final double latitude;
    private final List<Connection> connectionsFrom = new ArrayList<>();

    private final Map<Integer, List<Connection>> byHourConnections = new HashMap<>();

    public Stop(String name, double longitude, double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        Stop other = (Stop) obj;
        return other.getName().equals(name);
    }

    public void addConnection(Connection connection) {
        connectionsFrom.add(connection);
        byHourConnections.getOrDefault(connection.getDepartsAt().getHours(), new ArrayList<>()).add(connection);
    }
}
