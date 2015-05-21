package org.tendiwa.settlements.utils;

import org.jgrapht.EdgeFactory;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;
import org.tendiwa.math.BasketWithStones;
import org.tendiwa.math.StonesInBasketsProblem;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public final class NetworkGraphWithHolesInHull implements Graph2D {
	private final Random random;
	private final MeshedNetwork network;
	private final double probability;
	private final Graph2D graphWithHoles;

	public NetworkGraphWithHolesInHull(
		MeshedNetwork network,
		double probability,
		Random random
	) {
		if (probability < 0 || probability > 1) {
			throw new IllegalArgumentException("probability must be in [0..1] (now it is " + probability + ")");
		}
		this.network = network;
		this.probability = probability;
		this.random = new Random(random.nextInt());
		this.graphWithHoles = rejectRoads();
	}

	/**
	 * Removes some of the outer cycle edges
	 */
	private Graph2D rejectRoads() {
		MutableGraph2D modifiedGraph = new BasicMutableGraph2D(network);
		placeHolesOnChains(
			new HullSplitInChains(network).hullChains()
		)
			.stream()
			.flatMap(polyline -> polyline.toSegments().stream())
			.forEach(modifiedGraph::removeEdgeAndOrphanedVertices);
		return modifiedGraph;
	}

	/**
	 * Decides which sub-chains to remove from cycles.
	 *
	 * @param chains
	 * 	Chains of networks' cycles between those edges that have degree >=2 in the full graph.
	 */
	private List<HoleInHull> placeHolesOnChains(List<Polyline> chains) {
		StonesInBasketsProblem partition = new StonesInBasketsProblem(
			chains.stream()
				.mapToInt(polyline -> polyline.size() - 1)
				.toArray(),
			howManyEdgesToRemove(chains),
			random
		);
		return IntStream.range(0, chains.size())
			.mapToObj(
				i -> new HoleInHull(
					chains.get(i),
					partition.getBasket(i)
				)
			)
			.collect(toList());
	}

	private int howManyEdgesToRemove(List<Polyline> chains) {
		assert !chains.isEmpty();
		int numberOfEdgesInChains = chains.stream()
			.mapToInt(List::size)
			.sum();
		int numberOfEdgesToRemove = (int) Math.floor((double) numberOfEdgesInChains * probability);
		assert numberOfEdgesToRemove <= numberOfEdgesInChains;
		assert numberOfEdgesToRemove >= 0;
		return numberOfEdgesToRemove;
	}

	private final class HoleInHull extends Polyline_Wr {

		HoleInHull(
			Polyline hullPart,
			BasketWithStones basket
		) {
			super(
				new SegmentPolyline(
					hullPart.toSegments().subList(
						(int) Math.floor(random.nextDouble() * (basket.spaceLeft() + 1)),
						basket.stones()
					)
				)
			);
		}
	}

	@Override
	public int degreeOf(Point2D vertex) {
		return graphWithHoles.degreeOf(vertex);
	}

	@Override
	public Set<Segment2D> getAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return graphWithHoles.getAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graphWithHoles.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return graphWithHoles.getEdgeFactory();
	}

	@Override
	public Segment2D addEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graphWithHoles.addEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Segment2D segment2D) {
		return graphWithHoles.addEdge(sourceVertex, targetVertex, segment2D);
	}

	@Override
	public boolean addVertex(Point2D point2D) {
		return graphWithHoles.addVertex(point2D);
	}

	@Override
	public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graphWithHoles.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Segment2D segment2D) {
		return graphWithHoles.containsEdge(segment2D);
	}

	@Override
	public boolean containsVertex(Point2D point2D) {
		return graphWithHoles.containsVertex(point2D);
	}

	@Override
	public Set<Segment2D> edgeSet() {
		return graphWithHoles.edgeSet();
	}

	@Override
	public Set<Segment2D> edgesOf(Point2D vertex) {
		return graphWithHoles.edgesOf(vertex);
	}

	@Override
	public boolean removeAllEdges(Collection<? extends Segment2D> edges) {
		return graphWithHoles.removeAllEdges(edges);
	}

	@Override
	public Set<Segment2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return graphWithHoles.removeAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeAllVertices(Collection<? extends Point2D> vertices) {
		return graphWithHoles.removeAllVertices(vertices);
	}

	@Override
	public Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graphWithHoles.removeEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeEdge(Segment2D segment2D) {
		return graphWithHoles.removeEdge(segment2D);
	}

	@Override
	public boolean removeVertex(Point2D point2D) {
		return graphWithHoles.removeVertex(point2D);
	}

	@Override
	public Set<Point2D> vertexSet() {
		return graphWithHoles.vertexSet();
	}

	@Override
	public Point2D getEdgeSource(Segment2D segment2D) {
		return graphWithHoles.getEdgeSource(segment2D);
	}

	@Override
	public Point2D getEdgeTarget(Segment2D segment2D) {
		return graphWithHoles.getEdgeTarget(segment2D);
	}

	@Override
	public double getEdgeWeight(Segment2D segment2D) {
		return graphWithHoles.getEdgeWeight(segment2D);
	}

}
