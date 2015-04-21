package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepUnitedWithFromSideAlign implements Placement {
	private final IntimateRectanglePointer pointer;
	private final CardinalDirection fromSide;
	private final CardinalDirection alignSide;

	public StepUnitedWithFromSideAlign(IntimateRectanglePointer pointer, CardinalDirection fromSide, CardinalDirection alignSide) {
		this.pointer = pointer;
		this.fromSide = fromSide;
		this.alignSide = alignSide;
	}

	@Override
	public Rectangle placeIn(RectSet rectSet, RectangleSystemBuilder builder) {
		return shift(0).placeIn(rectSet, builder);
	}

	public Placement shift(final int shift) {
		return new Placement() {
			@Override
			public Rectangle placeIn(RectSet rectSet, RectangleSystemBuilder builder) {
				Rectangle placeableBounds = rectSet.bounds();
				Rectangle existingRec = builder.getRectangleByPointer(pointer).bounds();
				int staticCoord = existingRec.getStaticCoordOfSide(fromSide) + fromSide.getGrowing();
				if (fromSide == Directions.N) {
					staticCoord -= placeableBounds.height();
				} else if (fromSide == Directions.W) {
					staticCoord -= placeableBounds.width() - 1;
				}
				int dynamicCoord = (fromSide.isVertical() ? existingRec.getX() : existingRec.getY()) + shift * alignSide.getGrowing();
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
				return rectSet.place(builder, x, y);
			}
		};
	}
}
