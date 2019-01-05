package com.slowfrog.qwop.filter;

import com.slowfrog.qwop.RunInfo;
import com.slowfrog.qwop.genetic.Individual;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

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

        individual = new Individual(null, Arrays.asList(notCrashedInfo, crashedInfo));
        assertTrue("1/2 crash == 50%", fixture.matches(individual));

        individual = new Individual(null, Collections.singletonList(notCrashedInfo));
        assertFalse("0/2 crash", fixture.matches(individual));

        individual = new Individual(null, Collections.singletonList(crashedInfo));
        assertTrue("2/2 crash", fixture.matches(individual));
    }

    @Test
    public void min70PercentSuccessTest() {
        MinRatioFilter fixture = new MinRatioFilter(new NotFilter<>(new CrashedFilter()), 0.7F);

        Individual individual = new Individual(null, 0);
        assertFalse("empty runs should always return false", fixture.matches(individual));

        RunInfo crashedInfo1 = new RunInfo(null, 0, true, true, 0, 0);
        RunInfo crashedInfo2 = new RunInfo(null, 0, true, true, 0, 0);
        RunInfo crashedInfo3 = new RunInfo(null, 0, true, true, 0, 0);
        RunInfo notCrashedInfo1 = new RunInfo(null, 0, false, true, 0, 0);
        RunInfo notCrashedInfo2 = new RunInfo(null, 0, false, true, 0, 0);
        RunInfo notCrashedInfo3 = new RunInfo(null, 0, false, true, 0, 0);


        individual = new Individual(null, Arrays.asList(crashedInfo1, crashedInfo2, crashedInfo3));
        assertFalse("0/3 ratio", fixture.matches(individual));

        individual = new Individual(null, Arrays.asList(notCrashedInfo1, crashedInfo1, crashedInfo2));
        assertFalse("1/3 ratio", fixture.matches(individual));

        individual = new Individual(null, Arrays.asList(notCrashedInfo1, notCrashedInfo2, crashedInfo2));
        assertFalse("2/3 ratio", fixture.matches(individual));

        individual = new Individual(null, Arrays.asList(notCrashedInfo1, notCrashedInfo2, notCrashedInfo3));
        assertTrue("3/3 ratio", fixture.matches(individual));

        individual = new Individual(null, Arrays.asList(notCrashedInfo1, notCrashedInfo2, crashedInfo1, crashedInfo2));
        assertFalse("2/4 ratio", fixture.matches(individual));

        individual = new Individual(null, Arrays.asList(notCrashedInfo1, notCrashedInfo2, notCrashedInfo3, crashedInfo2));
        assertTrue("3/4 ratio", fixture.matches(individual));
    }
}
