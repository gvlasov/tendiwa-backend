package org.tendiwa.geometry;

public abstract class Bisector_Wr implements Bisector {
	private final Bisector bisector;

	protected Bisector_Wr(Bisector bisector) {
		this.bisector = bisector;
	}

	@Override
	public Vector2D asSumVector() {
		return bisector.asSumVector();
	}

	@Override
	public Vector2D asInbetweenVector() {
		return bisector.asInbetweenVector();
	}
}