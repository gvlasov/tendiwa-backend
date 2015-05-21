package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.MeshedNetwork_Wr;

public final class DefaultMeshedNetwork extends MeshedNetwork_Wr implements MeshedNetwork {

	public DefaultMeshedNetwork(Graph2D graph) {
		super(
			new MeshedNetworkBuilder(graph)
			.withDefaults()
			.build()
		);
	}
}
