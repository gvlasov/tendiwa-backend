package org.tendiwa.geometry;

import java.util.Optional;

public final class NamedRectSet extends RectSet_Wr {
	private final Optional<String> name;

	NamedRectSet(RectSet rectSet, Optional<String> name) {
		super(rectSet);
		this.name = name;
	}

	public Optional<String> name() {
		return name;
	}

	public boolean hasName(String name) {
		return this.name.isPresent() && this.name.get().equals(name);
	}
}
