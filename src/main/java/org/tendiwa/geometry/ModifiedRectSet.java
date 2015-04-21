package org.tendiwa.geometry;

import org.tendiwa.collections.Collectors;

import java.util.function.Function;

final class ModifiedRectSet extends RectSet_Wr {
	ModifiedRectSet(RectSet rectSet, Function<RectSet, RectSet> modification) {
		super(
			new BasicRectangleSequence(
				rectSet.parts().stream()
					.map(p -> new NamedRectSet(modification.apply(p), p.name()))
					.collect(Collectors.toImmutableList())
			)
		);
	}
}
