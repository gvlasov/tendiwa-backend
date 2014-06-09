package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.LinkedList;
import java.util.List;

class ListOfActiveVertices {
	final LinkedList<Node> nodes = new LinkedList<>();

	/**
	 * @param vertices
	 * 	List of points going counter-clockwise.
	 * @param edges
	 * 	List of edges going counter-clockwise.
	 */
	ListOfActiveVertices(List<Point2D> vertices, List<Segment2D> edges) {
		assert vertices.size() == edges.size();
		int l = vertices.size();
		Node previous = null;
		for (int i = 0; i < l; i++) {
			Node node = new Node(
				edges.get(i == 0 ? l - 1 : i - 1),
				edges.get(i),
				edges.get(i).start
			);
			if (i > 0) {
				node.connectWithPrevious(previous);
			}
			previous = node;
			nodes.add(node);
		}
		nodes.getFirst().connectWithPrevious(nodes.getLast());
		for (Node node : nodes) {
			node.computeReflexAndBisector();
		}
	}
}
