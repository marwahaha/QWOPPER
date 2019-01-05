package com.slowfrog.qwop;

import org.junit.Test;

import static com.slowfrog.qwop.Genetic.POPULATION_MAX_COLS;
import static com.slowfrog.qwop.Genetic.POPULATION_MAX_CONNECTIONS;
import static com.slowfrog.qwop.Genetic.POPULATION_MAX_ROWS;
import static com.slowfrog.qwop.Genetic.POPULATION_MAX_SIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WrapGridTest {

    @Test
    public void initTest() {
        WrapGrid<Individual> grid = new WrapGrid<>(POPULATION_MAX_ROWS, POPULATION_MAX_COLS, POPULATION_MAX_CONNECTIONS);

        assertEquals(POPULATION_MAX_ROWS, grid.getNumRows());
        assertEquals(POPULATION_MAX_COLS, grid.getNumCols());
        assertEquals(POPULATION_MAX_CONNECTIONS, grid.conNum);

        assertEquals((long) (POPULATION_MAX_ROWS) + POPULATION_MAX_COLS, POPULATION_MAX_SIZE);
        assertEquals((long) (POPULATION_MAX_ROWS) * POPULATION_MAX_COLS, grid.getLen());
        assertNotNull(grid.array);
    }

}
