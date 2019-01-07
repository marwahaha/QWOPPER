package com.slowfrog.qwop.genetic;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.slowfrog.qwop.genetic.EditDistance.levenshtein;
import static org.junit.Assert.assertEquals;

public class EvolutionTest {

    @Test
    public void testMutateString() {
        List<String> stringsToTest = Arrays.asList("BLAH", "asdf", "", "A", "Zeus", "supercali"); // TODO ENABLE 'supercalilongword' once memoized
        int timesToTest = 50;
        for (String str : stringsToTest) {
            for (int t = 0; t < timesToTest; t++) {
                System.out.println(str);
                System.out.println(Evolution.mutateString(str));
                assertEquals(1, levenshtein(str, Evolution.mutateString(str)));
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
