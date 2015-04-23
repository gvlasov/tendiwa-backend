package org.tendiwa.geometry;

import java.util.stream.Stream;

public abstract class Chain2D_Wr implements Chain2D {
	private final Chain2D chain;

	protected Chain2D_Wr(Chain2D chain) {
		this.chain = chain;
	}

	@Override
	public Stream<Segment2D> asSegmentStream() {
		return chain.asSegmentStream();
	}

	@Override
	public Stream<Point2D> asPointStream() {
		return chain.asPointStream();
	}

}