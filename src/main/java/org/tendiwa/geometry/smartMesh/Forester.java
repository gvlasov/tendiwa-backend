package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import static org.tendiwa.collections.Collectors.toLinkedHashSet;

final class Forester implements TreeCreator {
	private final Collection<OrientedCycle> innerCycles;
	private final OrientedCycle outerCycle;
	private final NetworkGenerationParameters config;
	private final Random random;
	private final Set<InnerTree> trees;
	private final DirectionDeviation directionDeviation;
	private final Forest forest;

	Forester(
		OrientedCycle outerCycle,
		Collection<OrientedCycle> innerCycles,
		NetworkGenerationParameters config,
		Random random
	) {
		this.innerCycles = innerCycles;
		this.outerCycle = outerCycle;
		this.config = config;
		this.random = random;
		this.forest = new Forest();
		this.directionDeviation = createDirectionDeviation();
		this.trees = growForest();
	}

	private DirectionDeviation createDirectionDeviation() {
		return config.favourAxisAlignedSegments ?
			new AxisAlignedDirectionDeviation(config) :
			new UnalignedDirectionDeviation(config, random);
	}

	private Set<InnerTree> growMissingTreesOnEnclosedCycles(Collection<OrientedCycle> enclosedCycles) {
		Set<InnerTree> missingTrees = new UnderplantedCyclesPlanting(
			enclosedCycles,
			this,
			deadEndSet,
			random
		).seedTreesAtUnderconnectedCycles();
		return growTreesUntilDepletion(missingTrees);
	}

	private Set<InnerTree> growForest() {
		Set<InnerTree> naturalForest = growNaturalRandomForest();
		Set<InnerTree> missingTrees = growMissingTreesOnEnclosedCycles(innerCycles);
		return Sets.union(
			naturalForest,
			missingTrees
		);
	}

	private Set<InnerTree> growNaturalRandomForest() {
		Set<InnerTree> seeds = new OuterCyclePlanting(
			forest,
			outerCycle,
			directionDeviation,
			config,
			random
		).seeds();
		return growTreesUntilDepletion(seeds);
	}

	/**
	 * Cycles over trees and propagates each of them until all trees are depleted.
	 *
	 * @param trees
	 */
	private Set<InnerTree> growTreesUntilDepletion(Set<InnerTree> trees) {
		while (!trees.isEmpty()) {
			Iterator<InnerTree> iterator = trees.iterator();
			while (iterator.hasNext()) {
				InnerTree tree = iterator.next();
				if (tree.isDepleted()) {
					iterator.remove();
				} else {
					tree.propagate();
				}
			}
		}
		return trees;
	}

	Set<Point2D> treeRoots() {
		return trees.stream()
			.filter(InnerTree::isGrown)
			.map(InnerTree::root)
			.collect(toLinkedHashSet());
	}

	ImmutableSet<Segment2D> deadEnds() {
		return ImmutableSet.copyOf(deadEndSet.values());
	}


	@Override
	public InnerTree createTreeOnEnclosedCycle(OrientedCycle enclosedCycle, Point2D root) {
		return new InnerTree(
			enclosedCycle.deviatedAngleBisector(root, false),
			new OrientedCycleSector(enclosedCycle, root, false),
			forest,
			directionDeviation,
			config
		);
	}

	Forest createForest() {
		return forest;
	}
}