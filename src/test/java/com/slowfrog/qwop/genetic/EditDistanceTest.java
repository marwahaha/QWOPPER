package com.slowfrog.qwop.genetic;

import org.junit.Test;

import static com.slowfrog.qwop.genetic.EditDistance.compareCost;
import static com.slowfrog.qwop.genetic.EditDistance.cost;
import static com.slowfrog.qwop.genetic.EditDistance.levenshtein;
import static com.slowfrog.qwop.genetic.EditDistance.modifiedLevenshtein;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EditDistanceTest {

    @Test
    public void testLevenshtein() {
        assertEquals(4, levenshtein("", "qwop"));
        assertEquals(1, levenshtein("a", "b"));
        assertEquals(1, levenshtein("", "b"));
        assertEquals(1, levenshtein("ab", "b"));
        assertEquals(2, levenshtein("ac", "b"));
        assertEquals(3, levenshtein("kitten", "sitting"));
        assertEquals(4, levenshtein("abc", "defg"));
        assertEquals(1, levenshtein("abc", "abac"));
        assertEquals(1, levenshtein("supercali", "supercall"));
        assertEquals(1, levenshtein("BLAH", "BLKH"));
//        TODO ENABLE AFTER MEMOIZING
//      assertEquals(1, levenshtein("supercalilongline", "supercalilonglines"));
    }

    @Test
    public void testCost() {
        assertEquals(1, cost('A'));
        assertEquals(1, cost('B'));
        assertEquals(1, cost('C'));
        assertEquals(1, cost('D'));
        assertEquals(2, cost('E'));
        assertEquals(2, cost('F'));
        assertEquals(2, cost('G'));
        assertEquals(2, cost('H'));
        assertEquals(2, cost('I'));
        assertEquals(2, cost('J'));
        assertEquals(3, cost('K'));
        assertEquals(3, cost('L'));
        assertEquals(3, cost('M'));
        assertEquals(3, cost('N'));
        assertEquals(4, cost('O'));
        // the P should have a cost too
        assertEquals(1, cost('P'));
    }

    @Test
    public void testCostOfWord() {
        assertEquals(1, cost("A"));
        assertEquals(1, cost("B"));
        assertEquals(1, cost("C"));
        assertEquals(10, cost("OPOP"));
        assertEquals(0, cost(""));
        assertEquals(10, cost("HILL"));
    }

    @Test
    public void testCostInvalidInput() {
        try {
            cost('Q');
            fail();
        } catch (NullPointerException npe) {
            // success
        }
        try {
            cost('R');
            fail();
        } catch (NullPointerException npe) {
            // success
        }
        try {
            cost('Z');
            fail();
        } catch (NullPointerException npe) {
            // success
        }
        try {
            cost('q');
            fail();
        } catch (NullPointerException npe) {
            // success
        }
    }

    @Test
    public void testCompareCost() {
        assertEquals(2, compareCost('A', 'B'));
        assertEquals(1, compareCost('A', 'P'));
        assertEquals(4, compareCost('O', 'P'));
        assertEquals(3, compareCost('L', 'H'));
        assertEquals(3, compareCost('O', 'D'));
        assertEquals(2, compareCost('L', 'K'));
        assertEquals(1, compareCost('F', 'M'));
        assertEquals(4, compareCost('E', 'J'));
    }

    @Test
    public void testModifiedLevenshtein() {
        assertEquals(1, modifiedLevenshtein("A", ""));
        assertEquals(1, modifiedLevenshtein("", "A"));
        assertEquals(0, modifiedLevenshtein("", ""));
        assertEquals(0, modifiedLevenshtein("A", "A"));
        assertEquals(2, modifiedLevenshtein("B", "A"));
        assertEquals(4, modifiedLevenshtein("BOB", "AOA"));
        assertEquals(1, modifiedLevenshtein("BOB", "BOBA"));
        assertEquals(2, modifiedLevenshtein("BOB", "COB"));
        assertEquals(4, modifiedLevenshtein("MOO", "MOP"));
        assertEquals(3, modifiedLevenshtein("POO", "POD"));
        assertEquals(4, modifiedLevenshtein("OO", "POD"));
        assertEquals(3, modifiedLevenshtein("OO", "OD"));
        assertEquals(7, modifiedLevenshtein("OO", "PD"));
        assertEquals(4, modifiedLevenshtein("PP", "POP"));
        assertEquals(1, modifiedLevenshtein("OO", "OPO"));
        assertEquals(2, modifiedLevenshtein("", "PP"));
    }
}
