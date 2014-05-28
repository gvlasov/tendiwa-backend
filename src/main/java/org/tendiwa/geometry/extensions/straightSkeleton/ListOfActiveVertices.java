package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.LineIntersection;

import java.util.LinkedList;
import java.util.List;

class ListOfActiveVertices {
	final LinkedList<Node> nodes = new LinkedList<>();

	/**
	 * @param vertices
	 * 	List of points going counter-clockwise.
	 * @param edges
	 * 	List of edges goint counter-clockwise.
	 */
	ListOfActiveVertices(List<Point2D> vertices, List<Segment2D> edges) {
		assert vertices.size() == edges.size();
		int l = vertices.size();
		for (int i = 0; i < l; i++) {
			nodes.add(
				new Node(
					edges.get(i == 0 ? l-1 : i - 1),
					edges.get(i),
					i == 0 ? null : nodes.get(i - 1)
				)
			);
		}
		nodes.getFirst().connect(nodes.getLast());

	}


}
