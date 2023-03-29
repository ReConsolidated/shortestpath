package org.example;


import org.example.runners.*;
import org.example.timeUtils.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        DataLoader loader = new DataLoader();
        loader.load("connection_graph.csv");
        loader.sort();
        manualTests(loader);
        //automatedTests(loader);
    }

    private static void manualTests(DataLoader loader) {
        System.out.println("Podaj przystanek początkowy: ");
        Scanner scanner = new Scanner(System.in);
        String start = scanner.nextLine();
        Stop startStop = getStopByName(loader, start);

        System.out.println("Podaj przystanek końcowy: ");
        String end = scanner.nextLine();
        Stop endStop = getStopByName(loader, end);

        System.out.println("Podaj kryterium t (najkrótszy czas przejazdu) lub p (najmniej przesiadek): ");
        String criterion = scanner.nextLine();
        AlgorithmRunner runner;
        switch (criterion) {
            case "t" -> runner = new OptimisedWikiRunner();
            case "p" -> runner = new LeastChangesRunner();
            default -> throw new IllegalArgumentException("Niepoprawne kryterium: " + criterion);
        }
        System.out.println("Podaj godzinę początku podróży w formacie HH:MM:SS");
        String time = scanner.nextLine();
        String[] split = time.split(":");
        Time startTime = new Time(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));

        long beginCounting = System.currentTimeMillis();
        List<CameFrom> path = runner.findBestPath(startTime, startStop, endStop);
        long totalTime = System.currentTimeMillis() - beginCounting;
        printPath(path);
        System.out.println("Czas wykonania: " + totalTime + "ms");
    }

    private static Stop getStopByName(DataLoader loader, String start) {
        for (Stop stop : loader.getStops()) {
            if (stop.getName().equals(start)) {
                return stop;
            }
        }
        throw new IllegalArgumentException("No such stop");
    }

    private static void automatedTests(DataLoader loader) {
        List<RunResult> dijkstraResults = new ArrayList<>();
        List<RunResult> wikiRunnerResults = new ArrayList<>();
        List<RunResult> optimisedWikiRunnerResults = new ArrayList<>();
        List<RunResult> leastChangesRunnerResults = new ArrayList<>();
        for (int i = 0; i<10; i++) {
            for (int j = 0; j<10; j++) {
                Time time = getRandomTime();
                Stop start = getRandomStop(loader);
                Stop end = getRandomStop(loader);
                while (end.equals(start)) {
                    end = getRandomStop(loader);
                }
                try {
                    dijkstraResults.add(run(new DijkstraRunner(), start, end, time));
                    wikiRunnerResults.add(run(new WikiRunner(), start, end, time));
                    optimisedWikiRunnerResults.add(run(new OptimisedWikiRunner(), start, end, time));
                    leastChangesRunnerResults.add(run(new LeastChangesRunner(), start, end, time));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        System.out.printf(
                "WikiRunner results: realtime - %d, traveltime - %.2f, swaps: %d%n",
                wikiRunnerResults.stream().map(RunResult::runDuration).reduce(0L, Long::sum),
                wikiRunnerResults.stream().map(RunResult::travelTime).reduce(0.0, Double::sum),
                wikiRunnerResults.stream().map(RunResult::swaps).reduce(0, Integer::sum)
                );
        System.out.printf(
                "Optimised results: realtime - %d, traveltime - %.2f, swaps: %d%n",
                optimisedWikiRunnerResults.stream().map(RunResult::runDuration).reduce(0L, Long::sum),
                optimisedWikiRunnerResults.stream().map(RunResult::travelTime).reduce(0.0, Double::sum),
                optimisedWikiRunnerResults.stream().map(RunResult::swaps).reduce(0, Integer::sum)
                );
        System.out.printf(
                "Dijkstra results: realtime - %d, traveltime - %.2f, swaps: %d%n",
                dijkstraResults.stream().map(RunResult::runDuration).reduce(0L, Long::sum),
                dijkstraResults.stream().map(RunResult::travelTime).reduce(0.0, Double::sum),
                dijkstraResults.stream().map(RunResult::swaps).reduce(0, Integer::sum)
                );
        System.out.printf(
                "LeastChanges results: realtime - %d, traveltime - %.2f, swaps: %d%n",
                leastChangesRunnerResults.stream().map(RunResult::runDuration).reduce(0L, Long::sum),
                leastChangesRunnerResults.stream().map(RunResult::travelTime).reduce(0.0, Double::sum),
                leastChangesRunnerResults.stream().map(RunResult::swaps).reduce(0, Integer::sum)
                );
    }

    private static Stop getRandomStop(DataLoader loader) {
        return loader.getStops().get((int) (Math.random() * loader.getStops().size()));
    }

    private static Time getRandomTime() {
        return new Time((int) (Math.random() * 24), (int) (Math.random() * 60), (int) (Math.random() * 60));
    }

    private static RunResult run(AlgorithmRunner runner, Stop start, Stop end, Time startTime) {
        long startTimestamp = System.currentTimeMillis();

        System.out.println("Start: " + start);
        System.out.println("End: " + end);
        var result = runner.findBestPath(startTime, start, end);
        long totalTime = System.currentTimeMillis() - startTimestamp;
        printPath(result);
        System.out.println("Total time: " + totalTime);
        double timeTo = startTime.timeTo(result.get(0).connection().getArrivesAt());
        if (timeTo < 0) {
            timeTo += 24 * 60 * 60;
        }
        int swaps = countSwaps(result);
        return new RunResult(runner.getClass(), totalTime, timeTo, swaps);
    }

    private static int countSwaps(List<CameFrom> result) {
        int swaps = 0;
        for (int i = 0; i < result.size() - 1; i++) {
            if (!Objects.equals(result.get(i).connection().getLine(), result.get(i + 1).connection().getLine())) {
                swaps++;
            }
        }
        return swaps;
    }

    static void printPath(List<CameFrom> path) {
        for (int i = path.size()-1; i>=0; i--) {
            CameFrom from = path.get(i);
            System.out.println(from.connection().getFrom() + " > " + from.connection().getTo() + ", " + from.connection());
        }
    }
}