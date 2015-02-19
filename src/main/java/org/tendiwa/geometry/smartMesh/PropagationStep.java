package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

interface PropagationStep {
	/**
	 * @return
	 * @throws java.lang.UnsupportedOperationException
	 * 	if {@link #createsNewSegment()} is false.
	 */
	Point2D target();

	boolean isTerminal();

	boolean createsNewSegment();
}
