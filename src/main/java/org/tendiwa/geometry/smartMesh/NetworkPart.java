package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.graphs.graphs2d.Graph2D;

public interface NetworkPart {
	public default void integrate(CutSegment2D cutSegment) {
		graph().removeEdge(cutSegment.originalSegment());
		graph().integrateCutSegment(cutSegment);
	}

	public Graph2D graph();
}
