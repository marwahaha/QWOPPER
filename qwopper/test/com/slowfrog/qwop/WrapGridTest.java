package com.slowfrog.qwop;

import static com.slowfrog.qwop.Genetic.POPULATION_MAX_COLS;
import static com.slowfrog.qwop.Genetic.POPULATION_MAX_CONNECTIONS;
import static com.slowfrog.qwop.Genetic.POPULATION_MAX_ROWS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
public class WrapGridTest {

	@Test
	public void initTest() {
		WrapGrid<Individual> grid = new WrapGrid<Individual>(POPULATION_MAX_ROWS, POPULATION_MAX_COLS, POPULATION_MAX_CONNECTIONS);
		
		assertEquals(POPULATION_MAX_ROWS, grid.row());
		assertEquals(POPULATION_MAX_COLS, grid.col());
		assertEquals(POPULATION_MAX_CONNECTIONS, grid.conNum);
		//should POPULATION_MAX_SIZE be equal to * not +?
		assertEquals(POPULATION_MAX_ROWS * POPULATION_MAX_COLS, grid.len());
		assertNotNull(grid.array);
	}

}
