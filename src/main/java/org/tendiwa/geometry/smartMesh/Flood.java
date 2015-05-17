package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Sector;

import java.util.*;

import static org.tendiwa.collections.Collectors.toImmutableSet;

final class Flood {
	private final NetworkGenerationParameters config;
	private final Random random;
	private final DirectionDeviation directionDeviation;
	private final InnerNetwork network;
	private final CycleWithInnerCycles perforatedCycle;

	Flood(
		CycleWithInnerCycles perforatedCycle,
		NetworkGenerationParameters config,
		Random random
	) {
		this.perforatedCycle = perforatedCycle;
		this.config = config;
		this.random = random;
		this.directionDeviation = createDirectionDeviation();
		this.network = new InnerNetwork(perforatedCycle, config, random);
	}

	private DirectionDeviation createDirectionDeviation() {
		return config.favourAxisAlignedSegments ?
			new AxisAlignedDirectionDeviation(config) :
			new UnalignedDirectionDeviation(config, random);
	}

	/**
	 * Cycles over floods and propagates each of them until all floods are depleted.
	 *
	 * @param starts
	 */
	private void floodUntilDepletion(Set<FloodStart> starts) {
		Set<FloodPart> floodParts = initialPropagation(starts);
		while (!floodParts.isEmpty()) {
			Iterator<FloodPart> iterator = floodParts.iterator();
			while (iterator.hasNext()) {
				FloodPart flood = iterator.next();
				flood.pullNextSegmentStarts()
					.forEach(ray -> network.tryPlacingSegment(ray, Sector.FULL_CIRCLE));

				if (flood.isDepleted()) {
					iterator.remove();
				}
			}
		}
	}

	private Set<FloodPart> initialPropagation(Set<FloodStart> starts) {
		Set<FloodPart> floodParts = new LinkedHashSet<>(starts.size());
		starts.forEach(start -> {
			Optional<Ray> floodContinuation = network.tryPlacingFirstSegment(start);
			if (floodContinuation.isPresent()) {
				floodParts.add(createFlood(floodContinuation.get()));
			}
		});
		return floodParts;
	}

	private FloodPart createFlood(Ray floodContinuation) {
		return new FloodPart(
			floodContinuation,
			directionDeviation,
			config
		);
	}


	private FloodFromOuterCycle createMainFlooder() {
		return new FloodFromOuterCycle(
			perforatedCycle.enclosingCycle(),
			config,
			random
		);
	}

	private FloodFromMissingInnerCycles createMissingFlooder() {
		return new FloodFromMissingInnerCycles(
			perforatedCycle.innerCycles(),
			network.whereBranchesStuckIntoCycles()
				.flatMap(CutSegment2D::pointStream)
				.collect(toImmutableSet()),
			random
		);
	}

	InnerNetwork createNetwork() {
		Set<FloodStart> mainFloods = createMainFlooder().floods();
		floodUntilDepletion(mainFloods);
		Set<FloodStart> missingFloods = createMissingFlooder().floods();
		floodUntilDepletion(missingFloods);
		return network;
	}
}