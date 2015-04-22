package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepUnitedWithFromSide {
	private final RectanglePointer pointer;
	private final CardinalDirection fromSide;

	public StepUnitedWithFromSide(RectanglePointer pointer, CardinalDirection side) {

		this.pointer = pointer;
		this.fromSide = side;
	}

	public StepUnitedWithFromSideAlign align(CardinalDirection endSide) {
		return new StepUnitedWithFromSideAlign(pointer, fromSide, endSide);
	}

	public Placement inMiddle() {
		return (recTree, builder) -> {
			Rectangle existingRec = pointer.find(builder).bounds();
			Rectangle placeableBounds = recTree.bounds();
			int staticCoord = existingRec.side(fromSide).getStaticCoord() + fromSide.getGrowing();
			int dynamicCoord = (fromSide.isVertical() ? existingRec.x() : existingRec.y())
				+ (existingRec.side(fromSide).length() - placeableBounds.side(fromSide).length()) / 2;
			int x, y;
			if (fromSide.isVertical()) {
				x = dynamicCoord;
				y = staticCoord;
			} else {
				x = staticCoord;
				y = dynamicCoord;
			}
			int dx = x - placeableBounds.x();
			int dy = y - placeableBounds.y();
			return new RecTreeWithPrecomputedBounds(
				recTree.translate(dx, dy),
				placeableBounds.moveTo(x, y)
			);
		};
	}
}
