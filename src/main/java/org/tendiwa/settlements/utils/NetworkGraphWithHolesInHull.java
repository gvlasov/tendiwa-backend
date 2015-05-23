package org.tendiwa.settlements.utils;

import org.tendiwa.geometry.Polyline;
import org.tendiwa.geometry.Polyline_Wr;
import org.tendiwa.geometry.SegmentPolyline;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.graphs.graphs2d.ConstructedGraph2D;
import org.tendiwa.math.BasketWithStones;
import org.tendiwa.math.StonesInBasketsProblem;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public final class NetworkGraphWithHolesInHull extends ConstructedGraph2D {
	private final Random random;
	private final MeshedNetwork network;
	private final double probability;

	public NetworkGraphWithHolesInHull(
		MeshedNetwork network,
		double probability,
		Random random
	) {
		super(network);
		if (probability < 0 || probability > 1) {
			throw new IllegalArgumentException("probability must be in [0..1] (now it is " + probability + ")");
		}
		this.network = network;
		this.probability = probability;
		this.random = new Random(random.nextInt());
		rejectRoads();
	}

	/**
	 * Removes some of the outer cycle edges
	 */
	private void rejectRoads() {
		placeHolesOnChains(
			new HullSplitInChains(network).hullChains()
		)
			.stream()
			.flatMap(polyline -> polyline.toSegments().stream())
			.forEach(this::removeEdgeAndOrphanedVertices);
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
}
