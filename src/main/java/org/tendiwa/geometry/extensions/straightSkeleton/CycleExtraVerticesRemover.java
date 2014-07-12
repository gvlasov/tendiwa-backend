package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Lists;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CycleExtraVerticesRemover {
	/**
	 * For a list of vertices, returns a new list of the same vertices but without those that lie on the same line
	 * with their neighbors.
	 * <p>
	 * In a list of only two or less vertices none of them are considered lying on the same line with neighbors,
	 * though technically they are.
	 *
	 * @param vertices
	 * 	A list of vertices.
	 * @return A new list without extra vertices.
	 */
	public static List<Point2D> removeVerticesOnLineBetweenNeighbors(List<Point2D> vertices) {
		int l = vertices.size();
		if (l < 3) {
			return vertices;
		}
		Map<Integer, Point2D> nonRemovedVertices = new LinkedHashMap<Integer, Point2D>();
		for (int i = 0; i < l; i++) {
			nonRemovedVertices.put(i, vertices.get(i));
		}
		for (int i = 0; i < l; i++) {
			if (isOnLineBetweenPreviousAndNextNodes(
				vertices.get(i - 1 == -1 ? l - 1 : i - 1),
				vertices.get(i),
				vertices.get(i + 1 == l ? 0 : i + 1)
			)
				) {
				nonRemovedVertices.remove(i);
			}
		}
		assert nonRemovedVertices.size() > 0;
		vertices = Lists.newArrayList(nonRemovedVertices.values());
		return vertices;
	}

	private static boolean isOnLineBetweenPreviousAndNextNodes(Point2D previous, Point2D current, Point2D next) {
		return previous != null &&
			current.distanceToLine(
				new Segment2D(previous, next)
			) < SuseikaStraightSkeleton.EPSILON
			&& !isMiddlePointPointy(previous, current, next);
	}

	private static boolean isMiddlePointPointy(Point2D start, Point2D middle, Point2D end) {
		return Vector2D.fromStartToEnd(start, middle).dotProduct(
			Vector2D.fromStartToEnd(middle, end)
		) < 0;
	}
}