package org.example;

import org.example.runners.AlgorithmRunner;

public record RunResult(Class<? extends AlgorithmRunner> aClass, long runDuration, double travelTime, int swaps) {

}
