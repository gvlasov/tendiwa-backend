package org.tendiwa.settlements.buildings;

import org.junit.Test;

import java.util.Random;

// http://stackoverflow.com/questions/27625611/why-does-this-random-value-has-25-75-distribution-instead-of-50-50
public class DoubleMinNormal {
	@Test
	public void test() {

		int y = 0;
		int n = 0;
		Random r = new Random();
		for (int i = 0; i < 1000000; i++) {
			double randomValue = r.nextDouble();
			long lastBit = Double.doubleToLongBits(randomValue) & 5;
			if (lastBit == 1) {
				y++;
			} else {
				n++;
			}
		}
		System.out.println(y + " " + n);
	}
}
