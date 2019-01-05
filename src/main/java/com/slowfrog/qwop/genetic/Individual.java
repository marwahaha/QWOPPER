package com.slowfrog.qwop.genetic;

import com.slowfrog.qwop.RunInfo;

import java.util.ArrayList;
import java.util.List;

public class Individual {

    final String str;
    float fitness;
    List<RunInfo> runs;

    public Individual(String pstr, List<RunInfo> pruns) {
        this.str = pstr;
        if (pruns != null) {
            this.runs = new ArrayList<>(pruns);
        } else {
            this.runs = new ArrayList<>();
        }
    }

    public Individual(String pstr, float fit) {
        this.str = pstr;
        this.fitness = fit;
        this.runs = new ArrayList<>();
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
