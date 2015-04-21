package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;

abstract class RectSet_Wr implements RectSet {
	private final RectSet rectSet;

	RectSet_Wr(RectSet rectSet) {
		this.rectSet = rectSet;
	}

	@Override
	public Rectangle bounds() {
		return rectSet.bounds();
	}

	@Override
	public ImmutableCollection<NamedRectSet> parts() {
		return rectSet.parts();
	}

	@Override
	public RectSet part(String name) {
		return rectSet.part(name);
	}

	@Override
	public RectSet nestedPart(String name) {
		return rectSet.nestedPart(name);
	}
}
