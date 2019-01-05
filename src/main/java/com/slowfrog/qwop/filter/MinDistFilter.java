package com.slowfrog.qwop.filter;

import com.slowfrog.qwop.RunInfo;

public class MinDistFilter implements IFilter<RunInfo> {

    private final int minDist;

    public MinDistFilter(int minDist) {
        this.minDist = minDist;
    }

    @Override
    public boolean matches(RunInfo run) {
        return (run.getDistance() >= this.minDist);
    }

}
