package com.slowfrog.qwop.filter;

import com.slowfrog.qwop.Individual;
import com.slowfrog.qwop.RunInfo;

public class MinRatioFilter implements IFilter<Individual> {

    private final float minRatio;

    private final IFilter<RunInfo> filter;

    /**
     * defaults to minRatio of 0.5
     *
     * @param filter
     */
    public MinRatioFilter(IFilter<RunInfo> filter) {
        this(filter, 0.5f);
    }

    public MinRatioFilter(IFilter<RunInfo> filter, float minRatio) {
        this.filter = filter;
        this.minRatio = minRatio;
    }

    @Override
    public boolean matches(Individual individual) {
        if ((individual.getRuns().isEmpty()) && (minRatio > 0)) {
            return false;
        }
        int matchingRuns = 0;
        for (RunInfo run : individual.getRuns()) {
            if (filter.matches(run)) {
                ++matchingRuns;
            }
        }
        return (((float) matchingRuns) / individual.getRuns().size()) >= this.minRatio;
    }

}
