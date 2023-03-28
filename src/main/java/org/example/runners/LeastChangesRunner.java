package org.example.runners;

import org.example.CameFrom;
import org.example.Connection;
import org.example.Stop;
import org.example.timeUtils.Time;

import java.util.*;

public class LeastChangesRunner implements AlgorithmRunner{
    @Override
    public List<CameFrom> findBestPath(Time startTime, Stop start, Stop end) {
        Set<Stop> openSet = new HashSet<>();
        openSet.add(start);

        Time currentTime = startTime;

        double inf = Double.POSITIVE_INFINITY;


        Map<Stop, CameFrom> cameFrom = new HashMap<>();

        Map<Stop, Time> timeAtStop = new HashMap<>();

        Map<Stop, String> cameWithLine = new HashMap<>();

        Map<Stop, Double> gScore = new HashMap<>();
        gScore.put(start, 0.0);

        Map<Stop, Double> fScore = new HashMap<>();
        fScore.put(start, heuristic(start, end));

        while (!openSet.isEmpty()) {
            Stop current = getLowestFscore(openSet, fScore);
            if (current.equals(end)) {
                return reconstructPath(cameFrom, new CameFrom(end, null));
            }

            openSet.remove(current);
            for (Connection connection : current.getConnectionsFrom()) {
                if (connection.getDepartsAt().isLessThan(timeAtStop.getOrDefault(current, currentTime))) {
                    continue;
                }
                Stop neighbor = connection.getTo();

                double gsc = gScore.getOrDefault(current, inf);
                double tentativeGScore;
                if (gsc == inf) {
                    tentativeGScore = inf;
                } else {
                    String line = cameWithLine.get(connection.getFrom());
                    if (line == null) {
                        tentativeGScore = gsc;
                    } else {
                        if (line.equals(connection.getLine())) {
                            tentativeGScore = gsc;
                        } else {
                            tentativeGScore = gsc + 1.0;
                        }
                    }
                }

                if (tentativeGScore <
                        gScore.getOrDefault(neighbor, inf)) {
                    cameFrom.put(neighbor, new CameFrom(current, connection));
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, end));
                    timeAtStop.put(neighbor, connection.getArrivesAt());
                    cameWithLine.put(neighbor, connection.getLine());
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        throw new IllegalArgumentException("No path");
    }

    private List<CameFrom> reconstructPath(Map<Stop, CameFrom> cameFrom, CameFrom end) {
        List<CameFrom> result = new ArrayList<>();
        while (cameFrom.containsKey(end.stop())) {
            end = cameFrom.get(end.stop());
            result.add(end);
        }
        return result;
    }

    private Stop getLowestFscore(Set<Stop> openSet, Map<Stop, Double> fScore) {
        Stop currentLowest = null;
        Double currentScore = null;
        for (Stop stop : openSet) {
            Double value = fScore.getOrDefault(stop, Double.POSITIVE_INFINITY);
            if (currentLowest == null ||
                    value < currentScore) {
                currentLowest = stop;
                currentScore = value;
            }
        }
        return currentLowest;
    }

    private Double heuristic(Stop start, Stop end) {
        return Math.abs(distance(start.getLatitude(), start.getLongitude(),
                end.getLatitude(), end.getLongitude()));
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        return (dist);
    }


    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
