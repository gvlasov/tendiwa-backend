package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.EdgeFactory;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;

import java.util.Set;

public class MeshedNetwork_Wr implements MeshedNetwork {
	private final MeshedNetwork network;

	protected MeshedNetwork_Wr(MeshedNetwork network) {
		this.network = network;
	}


	@Override
	public Graph2D outerHull() {
		return network.outerHull();
	}

	@Override
	public ImmutableSet<Polygon> meshCells() {
		return network.meshCells();
	}

	@Override
	public int degreeOf(Point2D vertex) {
		return network.degreeOf(vertex);
	}

	@Override
	public Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return network.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return network.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Segment2D segment2D) {
		return network.containsEdge(segment2D);
	}

	@Override
	public boolean containsVertex(Point2D point2D) {
		return network.containsVertex(point2D);
	}

	@Override
	public Set<Segment2D> edgeSet() {
		return network.edgeSet();
	}

	@Override
	public Set<Segment2D> edgesOf(Point2D vertex) {
		return network.edgesOf(vertex);
	}

	@Override
	public Set<Point2D> vertexSet() {
		return network.vertexSet();
	}

	@Override
	public Point2D getEdgeSource(Segment2D segment2D) {
		return network.getEdgeSource(segment2D);
	}

	@Override
	public Point2D getEdgeTarget(Segment2D segment2D) {
		return network.getEdgeTarget(segment2D);
	}
}
