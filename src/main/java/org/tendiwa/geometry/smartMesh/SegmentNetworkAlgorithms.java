package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.jgrapht.Graphs;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import static java.util.stream.Collectors.toCollection;

final class OuterHull {

	private final MeshedNetwork network;

	OuterHull(MeshedNetwork network) {
		this.network = network;
	}

	public Graph2D graph() {
		MutableGraph2D graph = new BasicMutableGraph2D();
		network.filaments()
			.forEach(filament -> Graphs.addGraph(graph, filament));
		network.meshes()
			.stream()
			.flatMap(mesh -> mesh.edgeSet().stream())
			.collect(toCollection(HashMultiset::create))
			.entrySet()
			.stream()
			.filter(entry -> entry.getCount() == 1)
			.map(Multiset.Entry::getElement)
			.forEach(graph::addSegmentAsEdge);
		return graph;
	}
}
