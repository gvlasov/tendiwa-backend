package org.tendiwa.geometry.smartMesh;

/**
 * Describes how a line snaps to a vertex or an edge of {@link OriginalMeshCell}.
 */
interface PropagationEvent extends PropagationStep {
	void integrateInto(AppendableNetworkPart networkPart);
}
