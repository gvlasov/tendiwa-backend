package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepNearFromSide {

	private final RectanglePointer pointer;
	private final CardinalDirection fromSide;

	StepNearFromSide(RectanglePointer pointer, CardinalDirection side) {
		this.pointer = pointer;
		this.fromSide = side;
	}

	public StepNearFromSideAlign align(CardinalDirection endSide) {
		return new StepNearFromSideAlign(pointer, fromSide, endSide);
	}

	public Placement inMiddle() {
		return (rectSet, builder) -> {
			Rectangle existingRec = pointer.find(builder).bounds();
			Rectangle placeableBounds = rectSet.bounds();
			int staticCoord = existingRec.side(fromSide).getStaticCoord()
				+ (builder.borderWidth() + 1) * fromSide.getGrowing();
			int x, y;
			int dynamicCoord = (fromSide.isVertical() ? existingRec.x() : existingRec.y())
				+ (existingRec.side(fromSide).length() - placeableBounds.side(fromSide).length()) / 2;
			if (fromSide.isVertical()) {
				x = dynamicCoord;
				y = staticCoord;
			} else {
				x = staticCoord;
				y = dynamicCoord;
			}
			return new RecTreeWithPrecomputedBounds(
				rectSet.moveTo(x, y),
				placeableBounds.moveTo(x, y)
			);
		};
	}
}
