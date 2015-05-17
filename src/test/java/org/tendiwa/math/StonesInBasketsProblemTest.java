package org.tendiwa.math;

import org.junit.Test;

import java.util.Random;

public class StonesInBasketsProblemTest {
	@Test
	public void shouldRunWithoutError() {
		int[] array = {1, 200, 6, 140};
		for (int i = 0; i < 1000; i++) {
			new StonesInBasketsProblem(array, 347, new Random(i)).getBasket(0);
			new StonesInBasketsProblem(array, 1, new Random(i)).getBasket(0);
			new StonesInBasketsProblem(array, 340, new Random(i)).getBasket(0);
			new StonesInBasketsProblem(array, 50, new Random(i)).getBasket(0);
			new StonesInBasketsProblem(array, 0, new Random(i)).getBasket(0);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void stonesDontFitInBaskets() {
		int[] array = {1, 200, 6, 140};
		// 1000 > 1+200+6+140
		new StonesInBasketsProblem(array, 1000, new Random(0)).getBasket(0);
	}
}