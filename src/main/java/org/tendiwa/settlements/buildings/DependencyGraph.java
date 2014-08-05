package org.tendiwa.settlements.buildings;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.List;
import java.util.Map;
import java.util.Set;

final class DependencyGraph {
	final DirectedGraph<Architecture, DefaultEdge> graph =
		new DirectedWeightedMultigraph<>(DefaultEdge.class);
	final List<List<Architecture>> cycles;
	final List<Set<Architecture>> connectedComponents;


	DependencyGraph(Map<Architecture, ArchitecturePolicy> policies) {
		for (Map.Entry<Architecture, ArchitecturePolicy> e : policies.entrySet()) {
			ArchitecturePolicy policy = e.getValue();
			Architecture architecture = e.getKey();
			for (Architecture present : policy.presence) {
				graph.addEdge(architecture, present);
			}
			for (Architecture close : policy.closeEnough.keySet()) {
				graph.addEdge(architecture, close);
			}
		}
		cycles = new SzwarcfiterLauerSimpleCycles<>(graph).findSimpleCycles();
		connectedComponents = new ConnectivityInspector<>(graph).connectedSets();
	}
}

