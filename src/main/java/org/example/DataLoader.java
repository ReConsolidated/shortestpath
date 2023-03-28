package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Getter;
import org.example.timeUtils.Time;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class DataLoader {
    private List<Stop> stops;
    private List<Connection> connections;

    public DataLoader() {

    }

    public void sort() {
        for (Stop stop : stops) {
            stop.getConnectionsFrom().sort((c1, c2) -> (int) c2.getDepartsAt().timeTo(c1.getDepartsAt()));
        }
    }

    public void load(String fileName) {
        stops = new ArrayList<>();
        connections = new ArrayList<>();

        List<List<String>> records = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName));) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        // Record consists of:
        // id, company, line, departure_time, arrival_time, start_stop,
        // end_stop, start_stop_lat, start_stop_lon, end_stop_lat, end_stop_lon

        System.out.println("Loading records");
        boolean ignoreFirst = false;
        for (var record : records) {
            if (!ignoreFirst) {
                ignoreFirst = true;
                continue;
            }
            long id = Long.parseLong(record.get(0));
            String company = record.get(1);
            String line = record.get(2);
            Time departureTime = Time.fromString(record.get(3));
            Time arrivalTime = Time.fromString(record.get(4));
            String startStopName = record.get(5);
            String endStopName = record.get(6);
            double startStopLatitude = Double.parseDouble(record.get(7));
            double startStopLongitude = Double.parseDouble(record.get(8));
            double endStopLatitude = Double.parseDouble(record.get(9));
            double endStopLongitude = Double.parseDouble(record.get(10));
            Stop startStop = getOrCreateStop(startStopName, startStopLatitude, startStopLongitude);
            Stop endStop = getOrCreateStop(endStopName, endStopLatitude, endStopLongitude);
            Connection connection = new Connection(id, line, startStop, endStop, departureTime, arrivalTime);
            connections.add(connection);
            startStop.addConnection(connection);
        }
        System.out.println("Finished loading records");
    }

    private Stop getOrCreateStop(String stopName, double latitude, double longitude) {
        Stop stop = new Stop(stopName, longitude, latitude);
        for (Stop s : stops) {
            if (s.equals(stop)) {
                return s;
            }
        }
        stops.add(stop);
        return stop;
    }
}
