package org.tendiwa.math;

import org.junit.Test;

import java.util.Random;

public class StonesInBasketsSolverTest {
	@Test
	public void shouldRunWithoutError() {
		int[] array = {1, 200, 6, 140};
		for (int i = 0; i < 1000; i++) {
			StonesInBasketsSolver.solve(array, 347, new Random(i));
			StonesInBasketsSolver.solve(array, 1, new Random(i));
			StonesInBasketsSolver.solve(array, 340, new Random(i));
			StonesInBasketsSolver.solve(array, 50, new Random(i));
			StonesInBasketsSolver.solve(array, 0, new Random(i));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentException() {
		int[] array = {1, 200, 6, 140};
		// 1000 > 1+200+6+140
		StonesInBasketsSolver.solve(array, 1000, new Random(0));
	}
}