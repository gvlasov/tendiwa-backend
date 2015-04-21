package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepNearFromSideAlign implements Placement {
	private final RectanglePointer pointer;
	private final CardinalDirection fromSide;
	private final CardinalDirection alignSide;

	StepNearFromSideAlign(RectanglePointer pointer, CardinalDirection fromSide, CardinalDirection alignSide) {
		this.pointer = pointer;
		this.fromSide = fromSide;
		this.alignSide = alignSide;
	}

	@Override
	public RectSet placeIn(RectSet rectSet, RectangleSystemBuilder builder) {
		return shift(0).placeIn(rectSet, builder);
	}

	public Placement shift(final int shift) {
		return (rectSet, builder) -> {
			Rectangle placeableBounds = rectSet.bounds();
			Rectangle existingRec = pointer.find(builder).bounds();
			int staticCoord = existingRec.side(fromSide).getStaticCoord()
				+ (builder.borderWidth() + 1)
				* fromSide.getGrowing();
			if (fromSide == Directions.N) {
				staticCoord -= placeableBounds.height();
			} else if (fromSide == Directions.W) {
				staticCoord -= placeableBounds.width() - 1;
			}
			int dynamicCoord = (fromSide.isVertical() ? existingRec.x() : existingRec.y())
				+ shift * alignSide.getGrowing();
			if (alignSide == Directions.E) {
				dynamicCoord += existingRec.width() - placeableBounds.width();
			} else if (alignSide == Directions.S) {
				dynamicCoord += existingRec.height() - placeableBounds.height();
			}
			int x, y;
			if (fromSide.isVertical()) {
				x = dynamicCoord;
				y = staticCoord;
			} else {
				x = staticCoord;
				y = dynamicCoord;
			}
			return new RectSetWithPrecomputedBounds(
				rectSet.moveTo(x, y),
				placeableBounds.moveTo(x, y)
			);
		};
	}
}
