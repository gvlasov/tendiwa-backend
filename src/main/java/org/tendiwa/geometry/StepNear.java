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
		return new Placement() {
			@Override
			public Rectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
				Rectangle placeableBounds = placeable.getBounds();
				Rectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
				int x, y;
				switch (corner) {
					case NE:
						x = existingRec.getX() + existingRec.getWidth() + builder.rs.getBorderWidth();
						y = existingRec.getY() - placeableBounds.getHeight() - builder.rs.getBorderWidth();
						break;
					case NW:
						x = existingRec.getX() - placeableBounds.getWidth() - builder.rs.getBorderWidth();
						y = existingRec.getY() - placeableBounds.getHeight() - builder.rs.getBorderWidth();
						break;
					case SE:
						x = existingRec.getX() + existingRec.getWidth() + builder.rs.getBorderWidth();
						y = existingRec.getY() + existingRec.getHeight() + builder.rs.getBorderWidth();
						break;
					case SW:
						y = existingRec.getY() + existingRec.getHeight() + builder.rs.getBorderWidth();
						x = existingRec.getX() - placeableBounds.getWidth() - builder.rs.getBorderWidth();
						break;
					default:
						throw new RuntimeException();
				}
				return placeable.place(builder, x, y);
			}
		};
	}
}
