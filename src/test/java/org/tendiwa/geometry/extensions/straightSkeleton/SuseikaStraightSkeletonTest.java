package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

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
			add(point2D(4, 30));
			add(point2D(41, 41));
			add(point2D(48, 11));
			add(point2D(95, 47));
			add(point2D(135, 10));
			add(point2D(156, 25));
			add(point2D(182, 10));
			add(point2D(197, 52));
			add(point2D(149, 80));
			add(point2D(180, 135));
			add(point2D(164, 195));
			add(point2D(133, 180));
			add(point2D(119, 195));
			add(point2D(87, 143));
			add(point2D(15, 190));
			add(point2D(31, 123));
			add(point2D(7, 112));
			add(point2D(50, 83));
		}};
		new SuseikaStraightSkeleton(vertices);
	}

	/**
	 * Mutates a given polygon, testing with each slightly different polygon that the algorithm runs without any error.
	 */
	public void manySimilarPolygons() {
		List<Point2D> points = new ArrayList<Point2D>() {{
			add(point2D(11, 14));
			add(point2D(26, 61));
			add(point2D(12, 92));
			add(point2D(78, 102));
			add(point2D(8, 166));
			add(point2D(62, 161));
			add(point2D(93, 185));
			add(point2D(125, 168));
			add(point2D(177, 186));
			add(point2D(160, 138));
			add(point2D(193, 122));
			add(point2D(142, 101));
			add(point2D(179, 91));
			add(point2D(147, 59));
			add(point2D(178, 6));
			add(point2D(89, 54));
			add(point2D(100, 13));
		}};
		IntStream.range(0, 180).forEach(i -> new SuseikaStraightSkeleton(
			points.stream().map(p -> {
				double angle = Math.PI * 2 / (180 / (points.indexOf(p) % 6 + 1)) * i;
				return point2D(
					p.x() + Math.cos(angle) * 6,
					p.y() + Math.sin(angle) * 6
				);
			}).collect(toList())
		));
	}
}
