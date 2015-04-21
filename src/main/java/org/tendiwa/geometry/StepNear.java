package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepNear {
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
					x = existingRec.x() + existingRec.width() + builder.borderWidth();
					y = existingRec.y() - placeableBounds.height() - builder.borderWidth();
					break;
				case NW:
					x = existingRec.x() - placeableBounds.width() - builder.borderWidth();
					y = existingRec.y() - placeableBounds.height() - builder.borderWidth();
					break;
				case SE:
					x = existingRec.x() + existingRec.width() + builder.borderWidth();
					y = existingRec.y() + existingRec.height() + builder.borderWidth();
					break;
				case SW:
					y = existingRec.y() + existingRec.height() + builder.borderWidth();
					x = existingRec.x() - placeableBounds.width() - builder.borderWidth();
					break;
				default:
					throw new RuntimeException();
			}
			return new RectSetWithPrecomputedBounds(
				rectSet.moveTo(x, y),
				placeableBounds.moveTo(x, y)
			);
		};
	}
}
