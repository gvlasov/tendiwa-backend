package org.tendiwa.geometry;

import static org.tendiwa.geometry.DSL.somewhere;

public final class StepPlaceNextAt {
	private final int count;
	private final RecTree recTree;

	StepPlaceNextAt(int count, RecTree recTree) {
		this.count = count;
		this.recTree = recTree;
	}

	public RecTree placingNextAt(final Placement where) {
		RecTreeBuilder newBuilder = new RecTreeBuilder();
		newBuilder.place(recTree, somewhere());
		for (int i = 1; i < count; i++) {
			newBuilder.place(recTree, where);
		}
		return newBuilder.done();
	}
}
