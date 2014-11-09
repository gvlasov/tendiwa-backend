package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.core.meta.Utils;
import org.tendiwa.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Algorithm for rebuilding a polygon that has more than 2 consecutive vertices with the same y-coordinate.
 * <p>
 * This algorithm creates a new polygon using a subset of the vertices of the old polygon.
 */
final class PolygonRebuilding {

	private final List<Point2D> polygon;
	private Point2D previousVertex;
	private boolean consecutive;
	private List<Point2D> answer;

	/**
	 * @param polygon
	 * 	A polygon to rebuild. This parameter will not be mutated by this method.
	 */
	private PolygonRebuilding(List<Point2D> polygon) {
		this.polygon = polygon;
	}

	/**
	 * Constructs a new list of points representing {@link #polygon} following these rules:
	 * <ol>
	 * <li>If there are more than 2 consecutive points with the same y-coordinate in the original polygon,
	 * don't use any of them other than the first and the last.</li>
	 * <li></li>
	 * </ol>
	 *
	 * @param startIndex
	 * 	Index of a vertex in the old polygon that will be the 0th index in the new polygon.
	 * @return A new polygon created from the old polygon.
	 */
	private List<Point2D> rebuildPolygon(int startIndex) {
		assert answer == null;
		int size = polygon.size();
		int maxListSize = size - 1; // We'll always reject at least 1 vertex
		answer = new ArrayList<>(maxListSize);
		previousVertex = polygon.get(Utils.previousIndex(size, startIndex));
//		assert previousVertex.y != polygon.get(startIndex).y;
		int preStartIndex = startIndex - 1;
		consecutive = false;
		for (int i = startIndex; i < size; i++) {
			operateOnVertex(i);
		}
		for (int i = 0; i < preStartIndex; i++) {
			operateOnVertex(i);
		}
		operateOnLastVertex(preStartIndex);
		return answer;
	}

	private void operateOnLastVertex(int index) {
		Point2D vertex = polygon.get(index);
		if (vertex.y == previousVertex.y) {
			answer.add(vertex);
		} else {
			operateOnVertex(index);
		}
	}

	private void operateOnVertex(int index) {
		Point2D vertex = polygon.get(index);
		if (vertex.y == previousVertex.y) {
			consecutive = true;
		} else {
			addIfConsecutive();
			consecutive = false;
			answer.add(vertex);
		}
		previousVertex = vertex;
	}

	private void addIfConsecutive() {
		if (consecutive) {
			answer.add(previousVertex);
		}
	}

	/**
	 * If a polygon contains 3 or more consecutive vertices that have the same y-coordinate, rebuilds that polygon so
	 * that consecutive horizontal edges are joined into a single edge.
	 *
	 * @param polygon
	 * 	A polygon to rebuild.
	 * @param indexWithMinX
	 * 	Index of the vertex of {@code polygon} that has the least x-coordinate of all vertices.
	 * @return The {@code polygon} parameter if it is not needed to rebuild it, or a new polygon if it is needed to
	 * rebuild it.
	 */
	static List<Point2D> rebuildIfBad(List<Point2D> polygon, int indexWithMinX) {
		assert polygon.stream().allMatch(v -> v.x >= polygon.get(indexWithMinX).x);
		int streak = 0;
		double lastYValue = Double.NaN;
		int size = polygon.size();
		for (int i = indexWithMinX; i < size; i++) {
			// From startIndex to size
			Point2D vertex = polygon.get(i);
			if (vertex.y == lastYValue) {
				streak++;
				if (streak == 3) {
					return new PolygonRebuilding(polygon).rebuildPolygon(indexWithMinX);
				}
			} else {
				streak = 1;
				lastYValue = vertex.y;
			}
		}
		// Code duplication is required here: it is the most sane solution right now.
		for (int i = 0; i < indexWithMinX; i++) {
			// Do exactly the same thing from 0 to startIndex
			Point2D vertex = polygon.get(i);
			if (vertex.y == lastYValue) {
				streak++;
				if (streak == 3) {
					return new PolygonRebuilding(polygon).rebuildPolygon(indexWithMinX);
				}
			} else {
				streak = 1;
				lastYValue = vertex.y;
			}
		}
		return polygon;
	}
}
