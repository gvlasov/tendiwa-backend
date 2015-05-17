package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.Mesh2D;
import org.tendiwa.geometry.graphs2d.PerforatedMesh2D;
import org.tendiwa.geometry.graphs2d.PolylineGraph2D;

public final class DefaultMeshedNetwork implements MeshedNetwork {

	private final SmartMeshedNetwork mesh;

	public DefaultMeshedNetwork(Graph2D graph) {
		this.mesh = new MeshedNetworkBuilder(graph)
			.withDefaults()
			.build();
	}


	@Override
	public ImmutableSet<PolylineGraph2D> filaments() {
		return mesh.filaments();
	}

	@Override
	public ImmutableSet<PerforatedMesh2D> meshes() {
		return mesh.meshes();
	}

	@Override
	public Graph2D fullGraph() {
		return mesh.fullGraph();
	}

	@Override
	public Graph2D outerHull() {
		return mesh.outerHull();
	}
}
