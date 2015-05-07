package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;

public abstract class MeshedNetwork_Wr implements MeshedNetwork {
	private final MeshedNetwork network;

	protected MeshedNetwork_Wr(MeshedNetwork network) {
		this.network = network;
	}

	@Override
	public ImmutableSet<Graph2D> filaments() {
		return network.filaments();
	}

	@Override
	public ImmutableSet<Mesh2D> meshes() {
		return network.meshes();
	}

	@Override
	public Graph2D fullGraph() {
		return network.fullGraph();
	}

	@Override
	public Graph2D outerHull() {
		return network.outerHull();
	}
}
