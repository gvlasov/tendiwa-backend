package org.tendiwa.geometry;

import static org.tendiwa.geometry.DSL.builder;
import static org.tendiwa.geometry.DSL.somewhere;

public final class StepPlaceNextAt {
	private final int count;
	private final RectSet rectSet;

	StepPlaceNextAt(int count, RectSet rectSet) {
		this.count = count;
		this.rectSet = rectSet;
	}

	public RectSet placingNextAt(final Placement where) {
		return new RectSet() {
			private RectangleSequence rs;
			public RectangleSystemBuilder prebuiltBuilder;

			@Override
			public Rectangle bounds() {
				if (rs == null) {
					throw new IllegalStateException();
				}
				return rs.bounds();
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
				newBuilder.place(rectSet, somewhere());
				for (int i = 1; i < count; i++) {
					newBuilder.place(rectSet, where);
				}
				prebuiltBuilder = newBuilder;
				rs = newBuilder.done();
			}

			@Override
			public RectSet rotate(Rotation rotation) {
				return rs.rotate(rotation);
			}

			@Override
			public Iterable<Rectangle> getRectangles() {
				return rs.getRectangles();
			}
		};
	}
}
