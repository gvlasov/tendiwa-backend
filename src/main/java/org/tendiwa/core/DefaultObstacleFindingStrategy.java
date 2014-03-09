package org.tendiwa.core;

import org.tendiwa.core.vision.ObstacleFindingStrategy;

/**
 * The only {@link org.tendiwa.core.vision.ObstacleFindingStrategy} used in Tendiwa engine.
 */
public class DefaultObstacleFindingStrategy implements ObstacleFindingStrategy {
private final Character character;

DefaultObstacleFindingStrategy(Character character) {
	this.character = character;
}

@Override
public boolean isCellBlockingVision(int x, int y) {
	return character.getPlane().getPassability(x, y) == Passability.NO;
}

@Override
public boolean isBorderBlockingVision(Border border) {
	return character.getPlane().hasBorderObject(border);
}
}
