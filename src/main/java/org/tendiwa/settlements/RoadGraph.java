package org.tendiwa.settlements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class RoadGraph implements UndirectedGraph<Point2D, Line2D> {
private final ImmutableMap<Point2D, LinkedList<Point2D>> adjacencyLists;

public RoadGraph(Point2D[] vertices, int[][] edges) {
	ImmutableMap.Builder<Point2D, LinkedList<Point2D>> adjacencyListsBuilder = ImmutableMap.builder();
	for (Point2D vertex : vertices) {
		adjacencyListsBuilder.put(vertex, new LinkedList<>());
	}
	adjacencyLists = adjacencyListsBuilder.build();
	for (int[] edge : edges) {
		addNewEdge(vertices[edge[0]], vertices[edge[1]]);
	}
}

public RoadGraph(Collection<Point2D> vertices, Collection<Line2D> edges) {
	ImmutableMap.Builder<Point2D, LinkedList<Point2D>> adjacencyListsBuilder = ImmutableMap.builder();
	for (Point2D vertex : vertices) {
		adjacencyListsBuilder.put(vertex, new LinkedList<>());
	}
	adjacencyLists = adjacencyListsBuilder.build();
	for (Line2D edge : edges) {
		addNewEdge(edge.start, edge.end);
	}
}

@Override
public Set<Line2D> getAllEdges(Point2D sourceVertex, Point2D targetVertex) {
	if (containsEdge(sourceVertex, targetVertex)) {
		return ImmutableSet.of(getEdge(sourceVertex, targetVertex));
	}
	return ImmutableSet.of();
}

@Override
public Line2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
	return getEdgeFactory().createEdge(sourceVertex, targetVertex);
}

@Override
public EdgeFactory<Point2D, Line2D> getEdgeFactory() {
	return new EdgeFactory<Point2D, Line2D>() {
		@Override
		public Line2D createEdge(final Point2D sourceVertex, final Point2D targetVertex) {
			return new Line2D(sourceVertex, targetVertex) {
				@Override
				public int hashCode() {
					return sourceVertex.hashCode() / 2 + targetVertex.hashCode() / 2;
				}
			};
		}
	};
}

/**
 * Only methods inside package can mutate RoadGraph, so it appears immutable to package user. That's why {@link
 * org.jgrapht.UndirectedGraph#addEdge(Object, Object, Object)} is not used to add edges.
 *
 * @param sourceVertex
 * @param targetVertex
 */
private void addNewEdge(Point2D sourceVertex, Point2D targetVertex) {
	adjacencyLists.get(sourceVertex).add(targetVertex);
	adjacencyLists.get(targetVertex).add(sourceVertex);
}

@Override
@Deprecated
public Line2D addEdge(Point2D c1, Point2D c2) {
	throw new UnsupportedOperationException();
}

@Override
@Deprecated
public boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Line2D defaultEdge) {
	throw new UnsupportedOperationException();
}

@Override
@Deprecated
public boolean addVertex(Point2D coordinate) {
	throw new UnsupportedOperationException();
}

@Override
public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
	return adjacencyLists.containsKey(sourceVertex) && adjacencyLists.get(sourceVertex).contains(targetVertex);
}

@Override
@Deprecated
public boolean containsEdge(Line2D defaultEdge) {
	throw new UnsupportedOperationException();
}

@Override
public boolean containsVertex(Point2D coordinate) {
	return adjacencyLists.containsKey(coordinate);
}

@Override
public Set<Line2D> edgeSet() {
	HashSet<Point2D> sources = new HashSet<>();
	ImmutableSet.Builder<Line2D> builder = ImmutableSet.builder();
	for (Point2D source : adjacencyLists.keySet()) {
		sources.add(source);
		for (Point2D target : adjacencyLists.get(source)) {
			if (sources.contains(target)) {
				continue;
			}
			builder.add(getEdgeFactory().createEdge(source, target));
		}
	}
	return builder.build();
}

@Override
public Set<Line2D> edgesOf(Point2D vertex) {
	ImmutableSet.Builder<Line2D> builder = ImmutableSet.builder();
	for (Point2D end : adjacencyLists.get(vertex)) {
		builder.add(getEdgeFactory().createEdge(vertex, end));
	}
	return builder.build();
}

@Override
public boolean removeAllEdges(Collection<? extends Line2D> edges) {
	throw new UnsupportedOperationException();
}

@Override
public Set<Line2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex) {
	throw new UnsupportedOperationException();
}

@Override
public boolean removeAllVertices(Collection<? extends Point2D> vertices) {
	throw new UnsupportedOperationException();
}

@Override
public Line2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
	throw new UnsupportedOperationException();
}

@Override
public boolean removeEdge(Line2D defaultEdge) {
	throw new UnsupportedOperationException();
}

@Override
public boolean removeVertex(Point2D coordinate) {
	throw new UnsupportedOperationException();
}

@Override
public Set<Point2D> vertexSet() {
	return adjacencyLists.keySet();
}

@Override
public Point2D getEdgeSource(Line2D edge) {
	return edge.start;
}

@Override
public Point2D getEdgeTarget(Line2D edge) {
	return edge.end;
}

@Override
public double getEdgeWeight(Line2D defaultEdge) {
	return 1;
}

@Override
public int degreeOf(Point2D vertex) {
	return adjacencyLists.get(vertex).size();
}
}
