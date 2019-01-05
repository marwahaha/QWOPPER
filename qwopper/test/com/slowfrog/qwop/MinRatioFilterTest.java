package com.slowfrog.qwop;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MinRatioFilterTest {

    @Test
    public void defaultRatioTest() {
        MinRatioFilter fixture = new MinRatioFilter(new CrashedFilter());

        Individual individual = new Individual(null, 0);
        assertFalse("empty runs should always return false", fixture.matches(individual));


        RunInfo crashedInfo = new RunInfo(null, 0, true, true, 0, 0);

        RunInfo notCrashedInfo = new RunInfo(null, 0, false, true, 0, 0);

        List<RunInfo> runs = new ArrayList<RunInfo>();
        runs.add(notCrashedInfo);
        runs.add(crashedInfo);


        individual = new Individual(null, runs);
        assertTrue("equal ratios", fixture.matches(individual));

        runs = new ArrayList<RunInfo>();
        runs.add(notCrashedInfo);

        individual = new Individual(null, runs);
        assertFalse("all pass", fixture.matches(individual));


        runs = new ArrayList<RunInfo>();
        runs.add(crashedInfo);

        individual = new Individual(null, runs);
        assertTrue("all fail", fixture.matches(individual));
    }

}
