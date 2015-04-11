package org.tendiwa.geometry.smartMesh;

import java.util.Random;

final class UnalignedDirectionDeviation implements DirectionDeviation {
	private final NetworkGenerationParameters config;
	private final Random random;

	UnalignedDirectionDeviation(
		NetworkGenerationParameters config,
		Random random
	) {
		this.config = config;
		this.random = random;
	}

	@Override
	public double deviateDirection(double direction) {
		double v = random.nextDouble();
		return direction
			- config.secondaryRoadNetworkDeviationAngle
			+ v * config.secondaryRoadNetworkDeviationAngle * 2;
	}
}
