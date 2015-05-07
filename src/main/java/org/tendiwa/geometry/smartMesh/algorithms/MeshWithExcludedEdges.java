package org.tendiwa.geometry.smartMesh.algorithms;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.smartMesh.SmartMeshedNetwork;
import org.tendiwa.graphs.graphs2d.Graph2D_Wr;

public final class MeshWithExcludedEdges extends Graph2D_Wr {
	public MeshWithExcludedEdges(SmartMeshedNetwork mesh) {
		super(mesh.fullGraph());
	}

	public ImmutableSet<Segment2D> excludedEdges() {
		throw new UnsupportedOperationException();
	}

}
