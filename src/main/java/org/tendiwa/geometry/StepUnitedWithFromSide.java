package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepUnitedWithFromSide {
	private final IntimateRectanglePointer pointer;
	private final CardinalDirection fromSide;

	public StepUnitedWithFromSide(IntimateRectanglePointer pointer, CardinalDirection side) {

		this.pointer = pointer;
		this.fromSide = side;
	}

	public StepUnitedWithFromSideAlign align(CardinalDirection endSide) {
		return new StepUnitedWithFromSideAlign(pointer, fromSide, endSide);
	}

	public Placement inMiddle() {
		return new Placement() {
			@Override
			public Rectangle placeIn(RectSet rectSet, RectangleSystemBuilder builder) {
				Rectangle existingRec = builder.getRectangleByPointer(pointer).bounds();
				Rectangle placeableBounds = rectSet.bounds();
				int staticCoord = existingRec.getStaticCoordOfSide(fromSide) + fromSide.getGrowing();
				int dynamicCoord = (fromSide.isVertical() ? existingRec.getX() : existingRec.getY()) + (existingRec.getDimensionBySide(fromSide) - placeableBounds.getDimensionBySide(fromSide)) / 2;
				int x, y;
				if (fromSide.isVertical()) {
					x = dynamicCoord;
					y = staticCoord;
				} else {
					x = staticCoord;
					y = dynamicCoord;
				}
				return rectSet.place(builder, x, y);
			}
		};
	}
}
