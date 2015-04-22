package org.tendiwa.drawing.extensions;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.RecTree;
import org.tendiwa.geometry.RecTree_Wr;
import org.tendiwa.geometry.Rectangle;

public final class RecTreeOfNeighbors extends RecTree_Wr {
	private final int borderWidth;
	private final UndirectedGraph<Rectangle, DefaultEdge> graph;

	public RecTreeOfNeighbors(RecTree recTree, int borderWidth) {
		super(recTree);
		this.graph = new SimpleGraph<>((a, b) -> new DefaultEdge());
		this.borderWidth = borderWidth;
	}
}
