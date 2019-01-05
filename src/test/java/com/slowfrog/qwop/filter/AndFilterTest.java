package com.slowfrog.qwop.filter;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AndFilterTest {

    @Test
    public void test() {
        IFilter<String> trueFilter = t -> true;
        IFilter<String> falseFilter = t -> false;

        AndFilter<String> trueAndFalse = new AndFilter<>(trueFilter, falseFilter);
        assertFalse(trueAndFalse.matches(null));

        AndFilter<String> falseAndTrue = new AndFilter<>(trueFilter, falseFilter);
        assertFalse(falseAndTrue.matches(null));

        AndFilter<String> falseAndFalse = new AndFilter<>(falseFilter, falseFilter);
        assertFalse(falseAndFalse.matches(null));

        AndFilter<String> trueAndTrue = new AndFilter<>(trueFilter, trueFilter);
        assertTrue(trueAndTrue.matches(null));
    }

}
