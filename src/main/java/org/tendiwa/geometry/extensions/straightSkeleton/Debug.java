package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Multimap;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawablePoint2D;
import org.tendiwa.drawing.extensions.DrawableSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;

import java.awt.Color;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

final class Debug {
	private final boolean debug = false;

	Debug() {
	}

	void drawSplitEventArc(SplitEvent event) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new DrawableSegment2D(
				segment2D(event.parent().vertex, event.point),
				Color.red
			)
		);
	}

	void drawEdgeEventArcs(Node leftParent, Node rightParent, Point2D point) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new DrawableSegment2D.Thin(
				segment2D(leftParent.vertex, point),
				Color.orange
			)
		);
		TestCanvas.canvas.draw(
			new DrawableSegment2D.Thin(
				segment2D(rightParent.vertex, point),
				Color.yellow
			)
		);
	}

	void testForNoIntersection(Multimap<Point2D, Point2D> arcs, Point2D start, Point2D end) {
		if (!debug) return;
		if (
			ShamosHoeyAlgorithm.areIntersected(
				arcs.entries().stream()
					.map(e -> segment2D(e.getKey(), e.getValue()))
					.collect(toList()))
			) {
			drawIntersectingArc(start, end);
			System.out.println(start);
			assert false;
		}
	}

	void drawEventHeight(SkeletonEvent event) {
		TestCanvas.canvas.draw(
			new DrawablePoint2D.Billboard(
				event.point,
				String.format("%1.6s", event.distanceToOriginalEdge),
				Color.black,
				Color.white
			)
		);
	}

	void drawIntersectingArc(Point2D start, Point2D end) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new DrawableSegment2D.Thin(
				segment2D(start, end),
				Color.white
			)
		);
	}

	void draw3NodeLavArcs(EdgeEvent point) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new DrawableSegment2D.Thin(
				segment2D(point.leftParent().vertex, point.point),
				Color.cyan
			)
		);
		TestCanvas.canvas.draw(
			new DrawableSegment2D.Thin(
				segment2D(point.rightParent().vertex, point.point),
				Color.cyan
			)
		);
		TestCanvas.canvas.draw(
			new DrawableSegment2D.Thin(
				segment2D(point.leftParent().previous().vertex, point.point),
				Color.cyan
			)
		);
	}

	public void draw2NodeLavArc(Node node1, Node node2) {
		if (!debug) return;
		TestCanvas.canvas.draw(
			new DrawableSegment2D.Thin(
				segment2D(node1.vertex, node2.vertex),
				Color.magenta
			)
		);
	}
}