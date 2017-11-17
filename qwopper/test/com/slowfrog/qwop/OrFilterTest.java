package com.slowfrog.qwop;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OrFilterTest {

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
		
		OrFilter<String> trueAndFalse = new OrFilter<String>(trueFilter, falseFilter);
		assertTrue(trueAndFalse.matches(null));
		
		OrFilter<String> falseAndTrue = new OrFilter<String>(trueFilter, falseFilter);
		assertTrue(falseAndTrue.matches(null));
		
		
		OrFilter<String> falseAndFalse = new OrFilter<String>(falseFilter, falseFilter);
		assertFalse(falseAndFalse.matches(null));
		
		OrFilter<String> trueAndTrue = new OrFilter<String>(trueFilter, trueFilter);
		assertTrue(trueAndTrue.matches(null));
	}

}
