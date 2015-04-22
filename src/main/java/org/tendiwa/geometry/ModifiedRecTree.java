package org.tendiwa.geometry;

import org.tendiwa.collections.Collectors;

import java.util.function.Function;

final class ModifiedRecTree extends RecTree_Wr {
	ModifiedRecTree(RecTree recTree, Function<RecTree, RecTree> modification) {
		super(
			new BasicRecTree(
				recTree.parts().stream()
					.map(p -> new NamedRecTree(modification.apply(p), p.name()))
					.collect(Collectors.toImmutableList())
			)
		);
	}
}
