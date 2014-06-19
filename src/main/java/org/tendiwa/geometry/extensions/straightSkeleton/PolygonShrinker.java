package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableList;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.*;

class PolygonShrinker {
	private final Map<Node, List<SkeletonEvent>> nodePaths = new LinkedHashMap<>();

	void setOriginalNodes(List<Node> nodes) {
		for (Node node : nodes) {
			nodePaths.put(node, new LinkedList<>());
		}
	}

	void addToNodePath(Node node, SkeletonEvent event) {
		nodePaths.get(node).add(event);
	}

	UndirectedGraph<Point2D, Segment2D> shrink(double depth) {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);
		ImmutableList<Node> orderedPolygonNodes = ImmutableList.copyOf(nodePaths.keySet());
		int size = orderedPolygonNodes.size();
		for (int i = 0; i < size; i++) {
			Node start = orderedPolygonNodes.get(i);
			Node end = orderedPolygonNodes.get(i + 1 == size ? 0 : i + 1);
			Point2D newStartPosition = getMovedNodePosition(start, depth, true);
			Point2D newEndPosition = getMovedNodePosition(end, depth, false);
			if (edgeShrinksToZero(newStartPosition, newEndPosition)) {
				return null;
			} else {
				return null;
			}
		}
		return null;
	}

	private boolean edgeShrinksToZero(Point2D edgeStart, Point2D edgeEnd) {
		return edgeStart.chebyshevDistanceTo(edgeEnd) < SuseikaStraightSkeleton.EPSILON;
	}

	private Point2D getMovedNodePosition(Node start, double depth, boolean isStart) {
		return null;
	}
}
