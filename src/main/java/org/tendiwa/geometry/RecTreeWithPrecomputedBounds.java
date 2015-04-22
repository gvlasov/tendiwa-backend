package org.tendiwa.geometry;

final class RecTreeWithPrecomputedBounds extends RecTree_Wr {

	private final Rectangle bounds;

	RecTreeWithPrecomputedBounds(RecTree recTree, Rectangle bounds) {
		super(recTree);
		this.bounds = bounds;
	}

	@Override
	public Rectangle bounds() {
		return bounds;
	}
}
