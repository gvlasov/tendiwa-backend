package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepAwayFromFromSideMargin {
	private final RectanglePointer pointer;
	private final CardinalDirection side;
	private final int margin;

	StepAwayFromFromSideMargin(RectanglePointer pointer, CardinalDirection side, int margin) {
		if (margin < 0) {
			throw new IllegalArgumentException("margin must be 0 or greater");
		}
		if (side == null) {
			throw new NullPointerException("side can't be null");
		}
		if (pointer == null) {
			throw new NullPointerException("pointer can't be null");
		}
		this.pointer = pointer;
		this.side = side;
		this.margin = margin;
	}

	public StepAwayFromFromSideMarginAlign align(CardinalDirection alignmentSide) {
		if (alignmentSide == null) {
			throw new NullPointerException("argument can't be null");
		}
		return new StepAwayFromFromSideMarginAlign(pointer, side, margin, alignmentSide);
	}

	public Placement inMiddle() {
		return new Placement() {
			@Override
			public RectSet placeIn(RectSet rectSet, RectangleSystemBuilder builder) {
				Rectangle placeableBounds = rectSet.bounds();
				Rectangle existingRec = pointer.find(builder).bounds();
				int staticCoord = existingRec.side(side).getStaticCoord()
					+ (builder.borderWidth() + 1 + margin) * side.getGrowing();
				if (side == Directions.N) {
					staticCoord -= placeableBounds.height();
				} else if (side == Directions.W) {
					staticCoord -= placeableBounds.width();
				}
				int dynamicCoord = (side.isVertical() ? existingRec.x() : existingRec.y())
					+ (existingRec.side(side).length() - placeableBounds.side(side).length()) / 2;
				int x, y;
				if (side.isVertical()) {
					x = dynamicCoord;
					y = staticCoord;
				} else {
					x = staticCoord;
					y = dynamicCoord;
				}
				int dx = x - placeableBounds.x();
				int dy = y - placeableBounds.y();

				return new RectSetWithPrecomputedBounds(
					rectSet.translate(dx, dy),
					placeableBounds.translate(dx, dy)
				);
			}
		};
	}

}
