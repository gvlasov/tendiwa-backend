package org.tendiwa.geometry;

import org.tendiwa.core.*;

public final class StepNear {
	private final RectanglePointer pointer;

	StepNear(RectanglePointer pointer) {
		this.pointer = pointer;
	}

	public StepNearFromSide fromSide(CardinalDirection side) {
		return new StepNearFromSide(pointer, side);
	}

	public Placement fromCorner(final OrdinalDirection corner) {
		return (rectSet, builder) -> {
			Rectangle placeableBounds = rectSet.bounds();
			Rectangle existingRec = pointer.find(builder).bounds();
			int x, y;
			switch (corner) {
				case NE:
					x = existingRec.x() + existingRec.width();
					y = existingRec.y() - placeableBounds.height();
					break;
				case NW:
					x = existingRec.x() - placeableBounds.width();
					y = existingRec.y() - placeableBounds.height();
					break;
				case SE:
					x = existingRec.x() + existingRec.width();
					y = existingRec.y() + existingRec.height();
					break;
				case SW:
					y = existingRec.y() + existingRec.height();
					x = existingRec.x() - placeableBounds.width();
					break;
				default:
					throw new RuntimeException();
			}
			return new RecTreeWithPrecomputedBounds(
				rectSet.moveTo(x, y),
				placeableBounds.moveTo(x, y)
			);
		};
	}
}
