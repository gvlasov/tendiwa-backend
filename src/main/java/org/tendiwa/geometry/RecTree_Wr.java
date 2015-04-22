package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;

public abstract class RecTree_Wr implements RecTree {
	private final RecTree recTree;

	public RecTree_Wr(RecTree recTree) {
		this.recTree = recTree;
	}

	@Override
	public Rectangle bounds() {
		return recTree.bounds();
	}

	@Override
	public ImmutableCollection<NamedRecTree> parts() {
		return recTree.parts();
	}

	@Override
	public RecTree part(String name) {
		return recTree.part(name);
	}

	@Override
	public RecTree nestedPart(String name) {
		return recTree.nestedPart(name);
	}
}
