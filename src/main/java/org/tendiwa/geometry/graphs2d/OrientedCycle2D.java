package org.tendiwa.geometry.graphs2d;

import org.tendiwa.geometry.Segment2D;

interface OrientedCycle2D extends Cycle2D {
	boolean isClockwise(Segment2D edge);
}
