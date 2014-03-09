package org.tendiwa.geometry;

import static org.tendiwa.geometry.DSL.builder;
import static org.tendiwa.geometry.DSL.somewhere;

public class StepPlaceNextAt {
private final int count;
private final Placeable placeable;

StepPlaceNextAt(int count, Placeable placeable) {
	this.count = count;
	this.placeable = placeable;
}

public Placeable placingNextAt(final Placement where) {
	return new Placeable() {
		private RectangleSequence rs;
		public RectangleSystemBuilder prebuiltBuilder;

		@Override
		public Rectangle getBounds() {
			if (rs == null) {
				throw new IllegalStateException();
			}
			return rs.getBounds();
		}

		@Override
		public Rectangle place(RectangleSystemBuilder builder, int x, int y) {
			return rs.place(builder, x, y);
		}

		@Override
		public StepPlaceNextAt repeat(int count) {
			return new StepPlaceNextAt(count, this);
		}

		@Override
		public void prebuild(RectangleSystemBuilder builder) {
			RectangleSystemBuilder newBuilder = builder(builder.rs.getBorderWidth());
			newBuilder.place(placeable, somewhere());
			for (int i = 1; i < count; i++) {
				newBuilder.place(placeable, where);
			}
			prebuiltBuilder = newBuilder;
			rs = newBuilder.done();
		}

		@Override
		public Placeable rotate(Rotation rotation) {
			return rs.rotate(rotation);
		}

		@Override
		public Iterable<Rectangle> getRectangles() {
			return rs.getRectangles();
		}
	};
}
}
