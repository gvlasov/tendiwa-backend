package org.tendiwa.demos.geometry;

import com.google.common.base.Stopwatch;
import org.tendiwa.demos.Demos;
import org.tendiwa.geometry.Cell;

public class DistanceDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(DistanceDemo.class);
	}

	@Override
	public void run() {
		Cell c1 = new Cell(45, 68);
		Cell c2 = new Cell(104, 198);
		Stopwatch watch = Stopwatch.createStarted();
		double j = 0;
		long ops = 900000000l;
		for (long i = 0; i < ops; i++) {
			c1.distanceDouble(c2);
		}
		System.out.println(watch);
		watch.reset().start();
		for (long i = 0; i < ops; i++) {
			c1.quickDistance(c2);
		}
		System.out.println(watch);
		watch.reset().start();
	}
}
