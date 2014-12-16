package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.*;

import static org.tendiwa.geometry.extensions.straightSkeleton.CycleExtraVerticesRemover.removeVerticesOnLineBetweenNeighbors;

class InitialListOfActiveVertices {
	final ArrayList<OriginalEdgeStart> nodes = new ArrayList<>();
	final List<Segment2D> edges;
	private final int size;

	/**
	 * @param vertices
	 * 	List of points going counter-clockwise.
	 */
	InitialListOfActiveVertices(List<Point2D> vertices, DrawableInto canvas) {
		vertices = removeVerticesOnLineBetweenNeighbors(vertices);
		edges = createEdgesBetweenVertices(vertices, canvas);
		assert vertices.size() == edges.size();
		createAndConnectNodes(edges);
		for (Node node : nodes) {
			node.computeReflexAndBisector();
		}
		this.size = edges.size();
	}

	int size() {
		return size;
	}

	private void createAndConnectNodes(List<Segment2D> edges) {
		int l = edges.size();
		OriginalEdgeStart previous = null;
		for (int i = 0; i < l; i++) {
			OriginalEdgeStart node = new OriginalEdgeStart(edges.get(i));
			if (i > 0) {
				node.setPreviousInLav(previous);
				node.setPreviousInitial(previous);
			}
			previous = node;
			nodes.add(node);
		}
		OriginalEdgeStart first = nodes.get(0);
		OriginalEdgeStart last = nodes.get(l - 1);
		first.setPreviousInLav(last);
		first.setPreviousInitial(last);
		nodes.forEach(OriginalEdgeStart::initFace);
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
			canvas.draw(edges.get(i), DrawingSegment2D.withColorThin(Color.RED));
		}
		return edges;
	}

}
