package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

interface PropagationStep {
	Point2D target();

	Point2D source();

	boolean isTerminal();

	boolean createsNewSegment();
}
