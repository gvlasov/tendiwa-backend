package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Multimap;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;

import java.awt.Color;
import java.util.stream.Collectors;

final class Debug {
	private final boolean debug = false;
	Debug() {
	}

	void drawSplitEventArc(SplitEvent event) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new Segment2D(event.parent().vertex, event.point),
			DrawingSegment2D.withColorThin(Color.red)
		);
	}

	void drawEdgeEventArcs(EdgeEvent event) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new Segment2D(event.leftParent().vertex, event.point),
			DrawingSegment2D.withColorThin(Color.yellow)
		);
		TestCanvas.canvas.draw(
			new Segment2D(event.rightParent().vertex, event.point),
			DrawingSegment2D.withColorThin(Color.yellow)
		);
	}
	void testForNoIntersection(Multimap<Point2D, Point2D> arcs, Point2D start, Point2D end) {
		if (!debug) return;
		if (
			ShamosHoeyAlgorithm.areIntersected(
				arcs.entries().stream().map(e -> new Segment2D(e.getKey(), e.getValue())).collect(Collectors.toList()))
			) {
			drawIntersectingArc(start, end);
			assert false;
		}
	}
	void drawIntersectingArc(Point2D start, Point2D end) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new Segment2D(start, end),
			DrawingSegment2D.withColorThin(Color.white)
		);
	}

	void draw3NodeLavArcs(EdgeEvent point) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new Segment2D(point.leftParent().vertex, point.point),
			DrawingSegment2D.withColorThin(Color.cyan)
		);
		TestCanvas.canvas.draw(
			new Segment2D(point.rightParent().vertex, point.point),
			DrawingSegment2D.withColorThin(Color.cyan)
		);
		TestCanvas.canvas.draw(
			new Segment2D(point.leftParent().previous().vertex, point.point),
			DrawingSegment2D.withColorThin(Color.cyan)
		);
	}

	public void draw2NodeLavArc(Node node1, Node node2) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new Segment2D(node1.vertex, node2.vertex),
			DrawingSegment2D.withColorThin(Color.magenta)
		);
	}
}