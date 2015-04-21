package org.tendiwa.geometry.smartMesh;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.awt.Color;

final class NonIntersectionTest {
	static void test(MutableGraph2D fullGraph, Segment2D newSegment) {
		if (ShamosHoeyAlgorithm.areIntersected(fullGraph.edgeSet())) {
			showIntersectedSegment(fullGraph, newSegment);
			assert false;
		}
	}

	private static void showIntersectedSegment(MutableGraph2D fullGraph, Segment2D newEdge) {
		TestCanvas.canvas.draw(
			newEdge,
			DrawingSegment2D.withColorDirected(Color.yellow, 0.5)
		);
		for (Segment2D existingEdge : fullGraph.edgeSet()) {
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
}