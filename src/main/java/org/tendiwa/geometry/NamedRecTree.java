package org.tendiwa.geometry;

import java.util.Optional;

public final class NamedRecTree extends RecTree_Wr {
	private final Optional<String> name;

	NamedRecTree(RecTree recTree, Optional<String> name) {
		super(recTree);
		this.name = name;
	}

	public Optional<String> name() {
		return name;
	}

	public boolean hasName(String name) {
		return this.name.isPresent() && this.name.get().equals(name);
	}
}
