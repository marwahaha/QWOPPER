package com.slowfrog.qwop.genetic;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.slowfrog.qwop.genetic.EditDistance.levenshtein;
import static com.slowfrog.qwop.genetic.EditDistance.modifiedLevenshtein;
import static com.slowfrog.qwop.genetic.Evolution.generateDescendants;
import static com.slowfrog.qwop.genetic.Evolution.generateDescendantsModified;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Note that the mutation tests don't demonstrate that all possible mutations are made,
 * just that all mutations made are (probabilistically) legal.
 */
public class EvolutionTest {

    private static final List<Double> insertionSettings = Arrays.asList(0.1, 0., 1., 0.3);
    private static final List<Double> deletionSettings = Arrays.asList(0.1, 0., 1., 0.3);
    // TODO ENABLE 'supercalilongword' once memoized
    private static final List<String> stringsToTest = Arrays.asList("BLAH", "GOOGLE", "A", "", "HAPNAP");
    private static final int timesToTest = 30;

    @Test
    public void testMutateString() {
        for (String str : stringsToTest) {
            for (int t = 0; t < timesToTest; t++) {
                System.out.println(str + " (Original)");
                for (int s = 0; s < insertionSettings.size(); s++) {
                    String mutStr = Evolution.mutateString(str, insertionSettings.get(s), deletionSettings.get(s));
                    System.out.println(mutStr + " (Mutated)");
                    assertEquals(1, levenshtein(str, mutStr));
                    assertFalse(mutStr.isEmpty());
                }
            }
        }
    }

    @Test
    public void testMutateStringModified() {
        for (String str : stringsToTest) {
            for (int t = 0; t < timesToTest; t++) {
                System.out.println(str + " (Original)");
                for (int s = 0; s < insertionSettings.size(); s++) {
                    String mutStr = Evolution.mutateStringModified(str, insertionSettings.get(s), deletionSettings.get(s));
                    System.out.println(mutStr + " (Mutated)");
                    assertEquals(1, modifiedLevenshtein(str, mutStr));
                    assertFalse(mutStr.isEmpty());
                }
            }
        }
    }

    @Test
    public void testGenerateDescendants() {
        for (String str : stringsToTest) {
            for (int t = 0; t < timesToTest; t++) {
                System.out.println(str + " (Original)");
                int maxNumDescendants = 10;

                for (int s = 0; s < insertionSettings.size(); s++) {
                    Set<String> descendants = generateDescendants(str, maxNumDescendants, insertionSettings.get(s), deletionSettings.get(s));
                    System.out.println(descendants);
                    assertTrue(descendants.size() <= maxNumDescendants);
                    for (String mutStr : descendants) {
                        assertEquals(1, levenshtein(str, mutStr));
                        assertFalse(mutStr.isEmpty());
                    }
                }
            }
        }
    }

    @Test
    public void testGenerateDescendantsModified() {
        for (String str : stringsToTest) {
            for (int t = 0; t < timesToTest; t++) {
                System.out.println(str + " (Original)");
                int maxNumDescendants = 10;

                for (int s = 0; s < insertionSettings.size(); s++) {
                    Set<String> descendants = generateDescendantsModified(str, maxNumDescendants, insertionSettings.get(s), deletionSettings.get(s));
                    System.out.println(descendants);
                    assertTrue(descendants.size() <= maxNumDescendants);
                    for (String mutStr : descendants) {
                        assertEquals(1, modifiedLevenshtein(str, mutStr));
                        assertFalse(mutStr.isEmpty());
                    }
                }
            }
        }
    }
}
