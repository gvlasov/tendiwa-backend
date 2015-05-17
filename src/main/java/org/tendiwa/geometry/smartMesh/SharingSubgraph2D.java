package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

interface SharingSubgraph2D extends MutableGraph2D {
	FullGraph supergraph();

	@Override
	default void integrateCutSegment(CutSegment2D cutSegment) {
		supergraph().splitSharedEdge(cutSegment);
	}
}
