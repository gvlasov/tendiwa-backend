package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.Mesh2D;

public interface MeshedNetwork {
	ImmutableSet<Graph2D> filaments();

	ImmutableSet<Mesh2D> meshes();

	Graph2D fullGraph();

	Graph2D outerHull();
}
