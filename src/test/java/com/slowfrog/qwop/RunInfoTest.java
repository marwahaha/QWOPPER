package com.slowfrog.qwop;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RunInfoTest {

    @Test
    public void initTest() {
        RunInfo info = new RunInfo("initTest", 3, true, false, 14, 2.5f);

        assertEquals("initTest", info.string);
        assertEquals(3, info.delay);
        assertTrue(info.crashed);
        assertFalse(info.stopped);
        assertEquals(14, info.duration);
        assertEquals(2.5f, info.distance, 0.0f);
    }

    @Test
    public void marshalUnMarshalTest() {
        RunInfo info = new RunInfo("initTestAgain", 3, true, false, 14, 2.5f);
        String marshal = info.marshal();
        RunInfo infoOut = RunInfo.unmarshal(marshal);

        assertEquals("initTestAgain", infoOut.string);
        assertEquals(3, infoOut.delay);
        assertTrue(infoOut.crashed);
        assertFalse(infoOut.stopped);
        assertEquals(14, infoOut.duration);
        assertEquals(2.5f, infoOut.distance, 0.0f);
    }

    @Test
    public void marshalUnMarshalTest2() {
        RunInfo info = new RunInfo("initTest2", 3, false, true, 14, 2.5f);
        String marshal = info.marshal();
        RunInfo infoOut = RunInfo.unmarshal(marshal);

        assertEquals("initTest2", infoOut.string);
        assertEquals(3, infoOut.delay);
        assertFalse(infoOut.crashed);
        assertTrue(infoOut.stopped);
        assertEquals(14, infoOut.duration);
        assertEquals(2.5f, infoOut.distance, 0.0f);
    }
}
