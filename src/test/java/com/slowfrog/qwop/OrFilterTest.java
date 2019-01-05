package com.slowfrog.qwop;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrFilterTest {

    @Test
    public void test() {
        IFilter<String> trueFilter = t -> true;
        IFilter<String> falseFilter = t -> false;

        OrFilter<String> trueAndFalse = new OrFilter<>(trueFilter, falseFilter);
        assertTrue(trueAndFalse.matches(null));

        OrFilter<String> falseAndTrue = new OrFilter<>(trueFilter, falseFilter);
        assertTrue(falseAndTrue.matches(null));

        OrFilter<String> falseAndFalse = new OrFilter<>(falseFilter, falseFilter);
        assertFalse(falseAndFalse.matches(null));

        OrFilter<String> trueAndTrue = new OrFilter<>(trueFilter, trueFilter);
        assertTrue(trueAndTrue.matches(null));
    }

}
