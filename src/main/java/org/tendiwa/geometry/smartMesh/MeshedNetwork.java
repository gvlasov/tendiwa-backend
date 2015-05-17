package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.Mesh2D;
import org.tendiwa.geometry.graphs2d.PerforatedMesh2D;
import org.tendiwa.geometry.graphs2d.PolylineGraph2D;

public interface MeshedNetwork {
	ImmutableSet<? extends PolylineGraph2D> filaments();

	ImmutableSet<? extends Mesh2D> meshes();

	Graph2D fullGraph();

	Graph2D outerHull();
}
