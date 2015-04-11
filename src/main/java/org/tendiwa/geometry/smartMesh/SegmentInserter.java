package org.tendiwa.geometry.smartMesh;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.SplitSegment2D;
import org.tendiwa.geometry.Vectors2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;

import java.awt.Color;

/**
 * Inserts new segments into {@link org.tendiwa.geometry.smartMesh.FullNetwork} and its subnetworks.
 */
public class SegmentInserter {
	private final FullNetwork fullNetwork;
	private final NetworkPart forest;

	public SegmentInserter(
		FullNetwork fullNetwork,
	) {
		this.fullNetwork = fullNetwork;
	}

	void addSecondaryNetworkEdge(Point2D source, Point2D target) {
		Segment2D newEdge = new Segment2D(source, target);
		TestCanvas.canvas.draw(
			newEdge,
			DrawingSegment2D.withColorThin(Color.blue)
		);
		forest.graph().addVertex(source);
		forest.graph().addVertex(target);
		forest.graph().addSegmentAsEdge(newEdge);
		fullNetwork.graph().addSegmentAsEdge(newEdge);
		fullNetwork.shareEdgeWithNetworkPart(newEdge, fullNetwork);
		fullNetwork.shareEdgeWithNetworkPart(newEdge, forest);
		assertNonIntersection(newEdge);
	}

	private void assertNonIntersection(Segment2D newEdge) {
		if (ShamosHoeyAlgorithm.areIntersected(fullNetwork.graph().edgeSet())) {
			showIntersectedSegment(newEdge);
			assert false;
		}
	}

	private void showIntersectedSegment(Segment2D newEdge) {
		TestCanvas.canvas.draw(
			newEdge,
			DrawingSegment2D.withColorDirected(Color.yellow, 0.5)
		);
		for (Segment2D existingEdge : fullNetwork.graph().edgeSet()) {
			if (ShamosHoeyAlgorithm.linesIntersect(newEdge, existingEdge)) {
				TestCanvas.canvas.draw(
					existingEdge,
					DrawingSegment2D.withColorDirected(Color.blue, 1)
				);
				System.out.println(existingEdge.intersection(newEdge) + " " + existingEdge.end);
				break;
			}
		}
	}

	void splitEdge(Segment2D segment, Point2D splitPoint) {
		assert !segment.end.equals(splitPoint) && !segment.start.equals(splitPoint);
		assert fullNetwork.graph().containsEdge(segment);
		minimumDistanceAssert(segment, splitPoint);
		fullNetwork.splitEdge(new SplitSegment2D(segment, splitPoint));
	}

	private void minimumDistanceAssert(Segment2D segment, Point2D point) {
		assert !segment.start.equals(point) : "point is start";
		assert !segment.end.equals(point) : "point is end";
		assert segment.start.distanceTo(point) > Vectors2D.EPSILON
			: segment.start.distanceTo(point) + " " + segment.start.distanceTo(segment.end);
		assert segment.end.distanceTo(point) > Vectors2D.EPSILON
			: segment.end.distanceTo(point) + " " + segment.start.distanceTo(segment.end);
	}
}
