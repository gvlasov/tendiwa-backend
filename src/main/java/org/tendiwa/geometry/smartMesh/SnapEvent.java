package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

/**
 * Describes how a line snaps to a vertex or an edge of {@link NetworkWithinCycle}.
 */
interface SnapEvent {

	Point2D target();

	boolean isTerminal();

	void integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter);
}
