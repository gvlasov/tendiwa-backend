package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepUnitedWithFromSideAlign implements Placement {
	private final RectanglePointer pointer;
	private final CardinalDirection fromSide;
	private final CardinalDirection alignSide;

	public StepUnitedWithFromSideAlign(
		RectanglePointer pointer,
		CardinalDirection fromSide,
		CardinalDirection alignSide
	) {
		this.pointer = pointer;
		this.fromSide = fromSide;
		this.alignSide = alignSide;
	}

	@Override
	public RecTree placeIn(RecTree recTree, RecTreeBuilder builder) {
		return shift(0).placeIn(recTree, builder);
	}

	public Placement shift(final int shift) {
		return new Placement() {
			@Override
			public RecTree placeIn(RecTree recTree, RecTreeBuilder builder) {
				Rectangle placeableBounds = recTree.bounds();
				Rectangle existingRec = pointer.find(builder).bounds();
				int staticCoord = existingRec.side(fromSide).getStaticCoord() + fromSide.getGrowing();
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
				return new RecTreeWithPrecomputedBounds(
					recTree.moveTo(x, y),
					placeableBounds.moveTo(x, y)
				);
			}
		};
	}
}
