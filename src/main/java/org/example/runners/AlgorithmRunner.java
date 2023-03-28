package org.example.runners;

import org.example.CameFrom;
import org.example.Stop;
import org.example.timeUtils.Time;

import java.util.List;

public interface AlgorithmRunner {
    List<CameFrom> findBestPath(Time startTime, Stop start, Stop end);
}
