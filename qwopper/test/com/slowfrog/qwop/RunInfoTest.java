package com.slowfrog.qwop;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RunInfoTest {

	@Test
	public void initTest() {
		RunInfo info = new RunInfo("initTest", 3, true, false, 14, 2.5f);
		
		assertEquals("initTest", info.string);
		assertEquals(3, info.delay);
		assertEquals(true, info.crashed);
		assertEquals(false, info.stopped);
		assertEquals(14, info.duration);
		assertEquals(2.5f, info.distance, 0.0f);
	}
	@Test
	public void marshalUnMarshelTest() {
		
		
		RunInfo info = new RunInfo("initTest", 3, true, false, 14, 2.5f);
		
		String marshal = info.marshal();
		
		RunInfo infoOut = RunInfo.unmarshal(marshal);
		assertEquals("initTest", infoOut.string);
		assertEquals(3, infoOut.delay);
		assertEquals(true, infoOut.crashed);
		assertEquals(false, infoOut.stopped);
		assertEquals(14, infoOut.duration);
		assertEquals(2.5f, infoOut.distance, 0.0f);
	}
	@Test
	public void marshalUnMarshelTest2() {
		
		
		RunInfo info = new RunInfo("initTest", 3, false, true, 14, 2.5f);
		
		String marshal = info.marshal();
		
		RunInfo infoOut = RunInfo.unmarshal(marshal);
		assertEquals("initTest", infoOut.string);
		assertEquals(3, infoOut.delay);
		assertEquals(false, infoOut.crashed);
		assertEquals(true, infoOut.stopped);
		assertEquals(14, infoOut.duration);
		assertEquals(2.5f, infoOut.distance, 0.0f);
	}
}
