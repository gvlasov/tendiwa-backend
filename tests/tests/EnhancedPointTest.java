package tests;

import static org.hamcrest.CoreMatchers.*;

import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;

import tendiwa.core.Directions;
import tendiwa.core.EnhancedPoint;
import tendiwa.core.Orientation;

public class EnhancedPointTest extends Assert {

	@Test
	public void testFromStaticAndDynamic() {
		assertEquals(
			EnhancedPoint.fromStaticAndDynamic(7, 9, Orientation.VERTICAL),
			new Point(7, 9));
		assertEquals(
			EnhancedPoint.fromStaticAndDynamic(7, 9, Orientation.HORIZONTAL),
			new Point(9, 7));
	}
	@Test
	public void testMove() throws Exception {
		EnhancedPoint point = new EnhancedPoint(6, 7)
			.moveToSide(Directions.E)
			.moveToSide(Directions.NW)
			.moveToSide(Directions.SW);
		assertEquals(point, new Point(5, 7));
		assertThat(point, is(not(new Point(4,7))));
	}

}
