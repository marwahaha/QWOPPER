package com.slowfrog.qwop.genetic;

import com.slowfrog.qwop.RunInfo;

import java.util.ArrayList;
import java.util.List;

public class Individual {

    final List<RunInfo> runs = new ArrayList<>();
    String str;
    float fitness;

    public Individual(String pstr) {
        this.str = pstr;
    }

    public Individual(List<RunInfo> pruns) {
        this.runs.addAll(pruns);
    }

    public Individual(String pstr, float fit) {
        this.str = pstr;
        this.fitness = fit;
    }

    public String getStr() {
        return str;
    }

    public float getFitness() {
        return fitness;
    }

    public List<RunInfo> getRuns() {
        return runs;
    }

}
