package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.jgrapht.Graphs;
import org.tendiwa.geometry.graphs2d.BasicPolylineGraph;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;
import org.tendiwa.graphs.graphs2d.ConstructedGraph2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import static java.util.stream.Collectors.toCollection;

final class OuterHull extends ConstructedGraph2D implements Graph2D {


	OuterHull(MeshedNetwork network) {
		network.minimumCycleBasis().filamentsSet()
			.forEach(filament -> this.addGraph(new BasicPolylineGraph(filament)));
		network.meshCells()
			.stream()
			.flatMap(polygon -> polygon.toSegments().stream())
			.collect(toCollection(HashMultiset::create))
			.entrySet()
			.stream()
			.filter(entry -> entry.getCount() == 1)
			.map(Multiset.Entry::getElement)
			.forEach(this::addSegmentAsEdge);
	}

}
