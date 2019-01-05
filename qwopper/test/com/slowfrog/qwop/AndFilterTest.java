package com.slowfrog.qwop;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AndFilterTest {

    @Test
    public void test() {
        IFilter<String> trueFilter = new IFilter<String>() {
            @Override
            public boolean matches(String t) {
                return true;
            }
        };

        IFilter<String> falseFilter = new IFilter<String>() {
            @Override
            public boolean matches(String t) {
                return false;
            }
        };

        AndFilter<String> trueAndFalse = new AndFilter<String>(trueFilter, falseFilter);
        assertFalse(trueAndFalse.matches(null));

        AndFilter<String> falseAndTrue = new AndFilter<String>(trueFilter, falseFilter);
        assertFalse(falseAndTrue.matches(null));


        AndFilter<String> falseAndFalse = new AndFilter<String>(falseFilter, falseFilter);
        assertFalse(falseAndFalse.matches(null));

        AndFilter<String> trueAndTrue = new AndFilter<String>(trueFilter, trueFilter);
        assertTrue(trueAndTrue.matches(null));
    }

}
