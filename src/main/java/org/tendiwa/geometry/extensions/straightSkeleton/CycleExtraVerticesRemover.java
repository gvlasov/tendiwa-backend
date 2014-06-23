package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Lists;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CycleExtraVerticesRemover {
	/**
	 * For a list of vertices, returns a new list of the same vertices but without those that line on the same line
	 * with their neighbors.
	 *
	 * @param vertices
	 * 	A list of vertices.
	 * @return A new list without extra vertices.
	 */
	public static List<Point2D> removeVerticesOnLineBetweenNeighbors(List<Point2D> vertices) {
		int l = vertices.size();
		Map<Integer, Point2D> nonRemovedVertices = new LinkedHashMap<Integer, Point2D>();
		for (int i = 0; i < l; i++) {
			nonRemovedVertices.put(i, vertices.get(i));
		}
		for (int i = 0; i < l; i++) {
			if (isOnLineBetweenPreviousAndNextNodes(
				vertices.get(i - 1 == -1 ? l - 1 : i - 1),
				vertices.get(i),
				vertices.get(i + 1 == l ? 0 : i + 1)
			)) {
				nonRemovedVertices.remove(i);
			}
		}
		vertices = Lists.newArrayList(nonRemovedVertices.values());
		return vertices;
	}

	private static boolean isOnLineBetweenPreviousAndNextNodes(Point2D previous, Point2D current, Point2D next) {
		return previous != null &&
			current.distanceToLine(
				new Segment2D(previous, next)
			) < SuseikaStraightSkeleton.EPSILON;
	}
}