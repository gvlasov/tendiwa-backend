package org.tendiwa.geometry.graphs2d;

import org.jgrapht.EdgeFactory;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class Cycle2D_Wr implements Cycle2D {
	private final Cycle2D cycle;

	public Cycle2D_Wr(Cycle2D cycle) {
		this.cycle = cycle;
	}

	@Override
	public Set<Segment2D> getAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return cycle.getAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return cycle.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return cycle.getEdgeFactory();
	}

	@Override
	public Segment2D addEdge(
		Point2D sourceVertex, Point2D targetVertex
	) {
		return cycle.addEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Segment2D segment2D) {
		return cycle.addEdge(sourceVertex, targetVertex, segment2D);
	}

	@Override
	public boolean addVertex(Point2D point2D) {
		return cycle.addVertex(point2D);
	}

	@Override
	public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return cycle.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Segment2D segment2D) {
		return cycle.containsEdge(segment2D);
	}

	@Override
	public boolean containsVertex(Point2D point2D) {
		return cycle.containsVertex(point2D);
	}

	@Override
	public Set<Segment2D> edgeSet() {
		return cycle.edgeSet();
	}

	@Override
	public Set<Segment2D> edgesOf(Point2D vertex) {
		return cycle.edgesOf(vertex);
	}

	@Override
	public boolean removeAllEdges(Collection<? extends Segment2D> edges) {
		return cycle.removeAllEdges(edges);
	}

	@Override
	public Set<Segment2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return cycle.removeAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeAllVertices(Collection<? extends Point2D> vertices) {
		return cycle.removeAllVertices(vertices);
	}

	@Override
	public Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
		return cycle.removeEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeEdge(Segment2D segment2D) {
		return cycle.removeEdge(segment2D);
	}

	@Override
	public boolean removeVertex(Point2D point2D) {
		return cycle.removeVertex(point2D);
	}

	@Override
	public Set<Point2D> vertexSet() {
		return cycle.vertexSet();
	}

	@Override
	public Point2D getEdgeSource(Segment2D segment2D) {
		return cycle.getEdgeSource(segment2D);
	}

	@Override
	public Point2D getEdgeTarget(Segment2D segment2D) {
		return cycle.getEdgeTarget(segment2D);
	}

	@Override
	public double getEdgeWeight(Segment2D segment2D) {
		return cycle.getEdgeWeight(segment2D);
	}

	@Override
	public boolean isClockwise() {
		return cycle.isClockwise();
	}

	@Override
	public int size() {
		return cycle.size();
	}

	@Override
	public boolean contains(Object o) {
		return cycle.contains(o);
	}

	@Override
	public Iterator<Point2D> iterator() {
		return cycle.iterator();
	}

	@Override
	public Point2D get(int index) {
		return cycle.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return cycle.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return cycle.lastIndexOf(o);
	}

	@Override
	public int degreeOf(Point2D vertex) {
		return cycle.degreeOf(vertex);
	}
}
