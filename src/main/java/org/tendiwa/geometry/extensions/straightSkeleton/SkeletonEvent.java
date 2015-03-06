package org.tendiwa.geometry.extensions.straightSkeleton;

import com.sun.istack.internal.NotNull;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;

abstract class SkeletonEvent implements Comparable<SkeletonEvent> {
	final double distanceToOriginalEdge;
	final Point2D point;

	/**
	 * <i>v<sub>a</sub></i> in [Obdrzalek 1998]
	 */

	SkeletonEvent(Point2D point, Node parent) {
		this.point = point;
		this.distanceToOriginalEdge = point.distanceToLine(parent.currentEdge);
		if (point.hashCode() == -2140443334) {
			TestCanvas.canvas.draw(parent.currentEdge, DrawingSegment2D.withColorDirected(Color.cyan, 1));
			TestCanvas.canvas.draw(
				point,
				DrawingPoint2D.withTextMarker(
					String.format("%1.6s", distanceToOriginalEdge),
					Color.black,
					Color.cyan
				)
			);
		} else if (point.hashCode() == -795039487) {
			TestCanvas.canvas.draw(parent.currentEdge, DrawingSegment2D.withColorDirected(Color.magenta, 1));
			TestCanvas.canvas.draw(
				point,
				DrawingPoint2D.withTextMarker(
					String.format("%1.6s", distanceToOriginalEdge),
					Color.white,
					Color.magenta
				)
			);
		}
	}

	@Override
	public int compareTo(@NotNull SkeletonEvent o) {
		if (distanceToOriginalEdge > o.distanceToOriginalEdge) {
			return 1;
		} else if (distanceToOriginalEdge < o.distanceToOriginalEdge) {
			return -1;
		}
		return 0;
	}

	abstract void handle(SuseikaStraightSkeleton skeleton);

}
