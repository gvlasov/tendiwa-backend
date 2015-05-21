package org.tendiwa.graphs.graphs2d;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;

import java.util.Collection;
import java.util.Set;

public abstract class ConstructedGraph2D implements Graph2D {
	@Override
	 Segment2D addEdge(
		Point2D sourceVertex, Point2D targetVertex
	) {
		return null;
	}

	@Override
	public boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Segment2D segment2D) {
		return false;
	}

	@Override
	public boolean addVertex(Point2D point2D) {
		return false;
	}

	@Override
	public boolean removeAllEdges(Collection<? extends Segment2D> edges) {
		return false;
	}

	@Override
	public Set<Segment2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return null;
	}

	@Override
	public boolean removeAllVertices(Collection<? extends Point2D> vertices) {
		return false;
	}

	@Override
	public Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
		return null;
	}

	@Override
	public boolean removeEdge(Segment2D segment2D) {
		return false;
	}

	@Override
	public boolean removeVertex(Point2D point2D) {
		return false;
	}
}
