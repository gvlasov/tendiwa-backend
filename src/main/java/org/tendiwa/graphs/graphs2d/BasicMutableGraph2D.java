package org.tendiwa.graphs.graphs2d;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;

import java.util.Collection;
import java.util.Set;

public class BasicMutableGraph2D implements MutableGraph2D {
	private final UndirectedGraph<Point2D, Segment2D> graph;

	public BasicMutableGraph2D() {
		this.graph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
	}

	/**
	 * Copies vertices and edges of another graph into this graph.
	 */
	public BasicMutableGraph2D(UndirectedGraph<Point2D, Segment2D> graph) {
		this.graph = new BasicMutableGraph2D();
		graph.vertexSet().forEach(this::addVertex);
		graph.edgeSet().forEach(this::addSegmentAsEdge);
	}

	@Override
	public final int degreeOf(Point2D vertex) {
		return graph.degreeOf(vertex);
	}

	@Override
	public final Set<Segment2D> getAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return graph.getAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public final Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public final EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return graph.getEdgeFactory();
	}

	@Override
	@Deprecated
	public final Segment2D addEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.addEdge(sourceVertex, targetVertex);
	}

	@Override
	@Deprecated
	public final boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Segment2D segment2D) {
		return graph.addEdge(sourceVertex, targetVertex, segment2D);
	}

	@Override
	public final boolean addVertex(Point2D point2D) {
		return graph.addVertex(point2D);
	}

	@Override
	public final boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public final boolean containsEdge(Segment2D segment2D) {
		return graph.containsEdge(segment2D);
	}

	@Override
	public final boolean containsVertex(Point2D point2D) {
		return graph.containsVertex(point2D);
	}

	@Override
	public final Set<Segment2D> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public final Set<Segment2D> edgesOf(Point2D vertex) {
		return graph.edgesOf(vertex);
	}

	@Override
	public boolean removeAllEdges(Collection<? extends Segment2D> edges) {
		return graph.removeAllEdges(edges);
	}

	@Override
	public final Set<Segment2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return graph.removeAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public final boolean removeAllVertices(Collection<? extends Point2D> vertices) {
		return graph.removeAllVertices(vertices);
	}

	@Override
	public final Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.removeEdge(sourceVertex, targetVertex);
	}

	@Override
	public final boolean removeEdge(Segment2D segment2D) {
		return graph.removeEdge(segment2D);
	}

	@Override
	public final boolean removeVertex(Point2D point2D) {
		return graph.removeVertex(point2D);
	}

	@Override
	public final Set<Point2D> vertexSet() {
		return graph.vertexSet();
	}

	@Override
	public final Point2D getEdgeSource(Segment2D segment2D) {
		return graph.getEdgeSource(segment2D);
	}

	@Override
	public final Point2D getEdgeTarget(Segment2D segment2D) {
		return graph.getEdgeTarget(segment2D);
	}

	@Override
	public final double getEdgeWeight(Segment2D segment2D) {
		return graph.getEdgeWeight(segment2D);
	}
}
