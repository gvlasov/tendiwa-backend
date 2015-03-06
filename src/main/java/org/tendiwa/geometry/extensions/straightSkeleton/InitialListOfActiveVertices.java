package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Lists;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.JTSUtils;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.tendiwa.geometry.extensions.straightSkeleton.CycleExtraVerticesRemover.removeVerticesOnLineBetweenNeighbors;

/**
 * From a list of vertices forming a polygon, creates
 * {@link org.tendiwa.geometry.extensions.straightSkeleton.OriginalEdgeStart}s and connects those
 * {@link org.tendiwa.geometry.extensions.straightSkeleton.OriginalEdgeStart}s with each other.
 */
class InitialListOfActiveVertices {
	final List<OriginalEdgeStart> nodes = new ArrayList<>();
	final List<Segment2D> edges;
	private final int size;

	/**
	 * @param vertices
	 * 	List of points going counter-clockwise.
	 */
	InitialListOfActiveVertices(List<Point2D> vertices, boolean trustCounterClockwise) {
		if (!trustCounterClockwise && !JTSUtils.isYDownCCW(vertices)) {
			vertices = Lists.reverse(vertices);
		}
		vertices = removeVerticesOnLineBetweenNeighbors(vertices);
		edges = createEdgesBetweenVertices(vertices);
		assert vertices.size() == edges.size();
		createAndConnectNodes(edges);
		nodes.forEach(Node::computeReflexAndBisector);
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

	private List<Segment2D> createEdgesBetweenVertices(List<Point2D> vertices) {
		int l = vertices.size();
		List<Segment2D> edges = new ArrayList<>(l);
		for (int i = 0; i < l; i++) {
			edges.add(
				new Segment2D(
					vertices.get(i),
					vertices.get(Utils.nextIndex(i, l))
				)
			);
		}
		return edges;
	}

}
