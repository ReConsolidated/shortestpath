package org.example;


import org.example.runners.AlgorithmRunner;
import org.example.runners.LeastChangesRunner;
import org.example.runners.OptimisedWikiRunner;
import org.example.runners.WikiRunner;
import org.example.timeUtils.Time;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        DataLoader loader = new DataLoader();
        loader.load("connection_graph.csv");

        run(new WikiRunner(), 100, 900, loader);
        loader.sort();
        run(new WikiRunner(), 100, 900, loader);
    }

    private static void run(AlgorithmRunner runner, int startId, int endId, DataLoader loader) {
        Stop start = loader.getStops().get(startId);
        Stop end = loader.getStops().get(endId);
        long startTimestamp = System.currentTimeMillis();
        Time startTime = new Time(23, 23, 11);

        System.out.println("Start: " + start);
        System.out.println("End: " + end);
        var result = runner.findBestPath(startTime, start, end);
        long totalTime = System.currentTimeMillis() - startTimestamp;
        printPath(result);
        System.out.println("Total time: " + totalTime);
    }

    static void printPath(List<CameFrom> path) {
        for (int i = path.size()-1; i>=0; i--) {
            CameFrom from = path.get(i);
            System.out.println(from.connection().getFrom() + " > " + from.connection().getTo() + ", " + from.connection());
        }
    }
}