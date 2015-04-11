package org.tendiwa.geometry.smartMesh;

import java.util.Random;

final class AxisAlignedDirectionDeviation implements DirectionDeviation {
	private final NetworkGenerationParameters config;

	AxisAlignedDirectionDeviation(
		NetworkGenerationParameters config
	) {
		this.config = config;
	}

	@Override
	public double deviateDirection(double direction) {
		double closestAxisParallelDirection = Math.round(direction / (Math.PI / 2)) * (Math.PI / 2);
		if (Math.abs(closestAxisParallelDirection - direction) < config.secondaryRoadNetworkDeviationAngle) {
			return closestAxisParallelDirection;
		} else {
			return direction +
				config.secondaryRoadNetworkDeviationAngle
					* Math.signum(closestAxisParallelDirection - direction);
		}
	}
}
