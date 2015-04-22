package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;

import java.util.Objects;
import java.util.function.Supplier;

public final class RectangleBuilderTemplate implements RecTree {

	private final Supplier<RecTree> howToBuild;
	private RecTree recTree;

	public RectangleBuilderTemplate(Supplier<RecTree> howTobuild) {
		Objects.requireNonNull(howTobuild);
		this.howToBuild = howTobuild;
	}

	@Override
	public ImmutableCollection<NamedRecTree> parts() {
		return getInstance().parts();
	}

	private RecTree getInstance() {
		if (recTree == null) {
			recTree = howToBuild.get();
		}
		return recTree;
	}

	@Override
	public Rectangle bounds() {
		return getInstance().bounds();
	}

	@Override
	public RecTree part(String name) {
		return getInstance().part(name);
	}

	@Override
	public RecTree nestedPart(String name) {
		return getInstance().nestedPart(name);
	}
}
