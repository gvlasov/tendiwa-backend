package org.tendiwa.geometry.smartMesh;

/**
 * Describes how a line snaps to a vertex or an edge of {@link NetworkWithinCycle}.
 */
interface SnapEvent extends PropagationStep {
	void integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter);
}
