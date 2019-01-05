package com.slowfrog.qwop;

import java.util.ArrayList;
import java.util.List;

public class Individual {

    protected final String str;
    protected List<RunInfo> runs;
    protected float fitness;

    public Individual(String pstr, List<RunInfo> pruns) {
        this.str = pstr;
        if (pruns != null) {
            this.runs = new ArrayList<RunInfo>(pruns);
        } else {
            this.runs = new ArrayList<RunInfo>();
        }
    }

    public Individual(String pstr, float fit) {
        this.str = pstr;
        this.fitness = fit;
        this.runs = new ArrayList<RunInfo>();
    }

}
