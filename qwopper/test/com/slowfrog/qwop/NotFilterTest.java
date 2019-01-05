package com.slowfrog.qwop;

import static org.junit.Assert.*;

import org.junit.Test;

public class NotFilterTest {

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
		
		NotFilter<String> notTrue = new NotFilter<String>(trueFilter);
		assertFalse(notTrue.matches(null));
		
		NotFilter<String> notFalse = new NotFilter<String>(falseFilter);
		assertTrue(notFalse.matches(null));
	}

}
