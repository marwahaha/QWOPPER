package com.slowfrog.qwop;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NotFilterTest {

    @Test
    public void test() {
        IFilter<String> trueFilter = t -> true;
        IFilter<String> falseFilter = t -> false;

        NotFilter<String> notTrue = new NotFilter<>(trueFilter);
        assertFalse(notTrue.matches(null));

        NotFilter<String> notFalse = new NotFilter<>(falseFilter);
        assertTrue(notFalse.matches(null));
    }

}
