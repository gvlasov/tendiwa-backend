package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

public class InitialNode extends Node {

	InitialNode(Point2D point) {
		super(previousEdge, currentEdge, point);
	}
}
