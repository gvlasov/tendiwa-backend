package org.tendiwa.core;

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
	return new Placement() {
		@Override
		public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
			EnhancedRectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
			EnhancedRectangle placeableBounds = placeable.getBounds();
			int staticCoord = existingRec.getStaticCoordOfSide(fromSide) + fromSide.getGrowing();
			int dynamicCoord = (fromSide.isVertical() ? existingRec.x : existingRec.y) + (existingRec.getDimensionBySide(fromSide) - placeableBounds.getDimensionBySide(fromSide)) / 2;
			int x, y;
			if (fromSide.isVertical()) {
				x = dynamicCoord;
				y = staticCoord;
			} else {
				x = staticCoord;
				y = dynamicCoord;
			}
			return placeable.place(builder, x, y);
		}
	};
}
}
