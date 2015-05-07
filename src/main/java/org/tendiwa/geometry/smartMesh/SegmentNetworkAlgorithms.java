package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.Mesh2D;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.geometry.smartMesh.OriginalMeshCell;
import org.tendiwa.geometry.smartMesh.SmartMeshedNetwork;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

final class OuterHull {

	private final MeshedNetwork network;

	OuterHull(MeshedNetwork network) {
		this.network = network;
	}

	public Graph2D graph() {
		MutableGraph2D graph = new MutableGraph2D();
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
