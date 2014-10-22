package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.*;

import static org.tendiwa.geometry.extensions.straightSkeleton.CycleExtraVerticesRemover.removeVerticesOnLineBetweenNeighbors;

class ListOfActiveVertices {
	final LinkedList<Node> nodes = new LinkedList<>();
	final List<Segment2D> edges;

	/**
	 * @param vertices
	 * 	List of points going counter-clockwise.
	 */
	ListOfActiveVertices(List<Point2D> vertices, DrawableInto canvas) {
		vertices = removeVerticesOnLineBetweenNeighbors(vertices);
		edges = createEdgesBetweenVertices(vertices, canvas);
		assert vertices.size() == edges.size();
		createAndConnectNodes(edges);
		for (Node node : nodes) {
			node.computeReflexAndBisector();
		}
	}

	private void createAndConnectNodes(List<Segment2D> edges) {
		int l = edges.size();
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
	}

	private List<Segment2D> createEdgesBetweenVertices(List<Point2D> vertices, DrawableInto canvas) {
		int l = vertices.size();
		List<Segment2D> edges = new ArrayList<>(l);
		for (int i = 0; i < l; i++) {
			edges.add(
				new Segment2D(
					vertices.get(i),
					vertices.get(i + 1 < l ? i + 1 : 0)
				)
			);
			canvas.draw(edges.get(i), DrawingSegment2D.withColor(Color.RED));
		}
		return edges;
	}

}
