package tests;

import java.awt.Point;

import org.junit.Test;


import tendiwa.core.meta.Range;
import tendiwa.core.FuckingTrailRectangleSystem;

public class TrailRectnagleSystemTest {

@Test
public void test() {
	int numberOfTests = 100;
	for (int i=0; i<numberOfTests; i++) {
		FuckingTrailRectangleSystem trs = new FuckingTrailRectangleSystem(10, new Range(1,15), new Point(12, 18))
			.buildToPoint(new Point(212, 32));
	}
}
}
