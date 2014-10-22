package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * This is a test for class {@link org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton} that has
 * been written well, but never used because it works only for a subset of all problems it is supposed to solve. That
 * clas and this test are not deleted just because I don't know if these will be useful in future or not.
 *
 * @author suseika
 */
public class SuseikaStraightSkeletonTest {
	public void nonConvexPolygonWithoutHoles() {
		List<Point2D> vertices = new ArrayList<Point2D>() {{
			add(new Point2D(4, 30));
			add(new Point2D(41, 41));
			add(new Point2D(48, 11));
			add(new Point2D(95, 47));
			add(new Point2D(135, 10));
			add(new Point2D(156, 25));
			add(new Point2D(182, 10));
			add(new Point2D(197, 52));
			add(new Point2D(149, 80));
			add(new Point2D(180, 135));
			add(new Point2D(164, 195));
			add(new Point2D(133, 180));
			add(new Point2D(119, 195));
			add(new Point2D(87, 143));
			add(new Point2D(15, 190));
			add(new Point2D(31, 123));
			add(new Point2D(7, 112));
			add(new Point2D(50, 83));
		}};
		new SuseikaStraightSkeleton(vertices);
	}

	/**
	 * Mutates a given polygon, testing with each slightly different polygon that the algorithm runs without any error.
	 */
	public void manySimilarPolygons() {
		List<Point2D> points = new ArrayList<Point2D>() {{
			add(new Point2D(11, 14));
			add(new Point2D(26, 61));
			add(new Point2D(12, 92));
			add(new Point2D(78, 102));
			add(new Point2D(8, 166));
			add(new Point2D(62, 161));
			add(new Point2D(93, 185));
			add(new Point2D(125, 168));
			add(new Point2D(177, 186));
			add(new Point2D(160, 138));
			add(new Point2D(193, 122));
			add(new Point2D(142, 101));
			add(new Point2D(179, 91));
			add(new Point2D(147, 59));
			add(new Point2D(178, 6));
			add(new Point2D(89, 54));
			add(new Point2D(100, 13));
		}};
		IntStream.range(0, 180).forEach(i -> new SuseikaStraightSkeleton(
			points.stream().map(p -> {
				double angle = Math.PI * 2 / (180 / (points.indexOf(p) % 6 + 1)) * i;
				return new Point2D(
					p.x + Math.cos(angle) * 6,
					p.y + Math.sin(angle) * 6
				);
			}).collect(toList())
		));
	}
}
