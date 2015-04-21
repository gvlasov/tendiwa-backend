package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;

import java.util.Objects;
import java.util.function.Supplier;

public final class RectangleBuilderTemplate implements RectSet {

	private final Supplier<RectSet> howToBuild;
	private RectSet rectSet;

	public RectangleBuilderTemplate(Supplier<RectSet> howTobuild) {
		Objects.requireNonNull(howTobuild);
		this.howToBuild = howTobuild;
	}

	@Override
	public ImmutableCollection<NamedRectSet> parts() {
		return getInstance().parts();
	}

	private RectSet getInstance() {
		if (rectSet == null) {
			rectSet = howToBuild.get();
		}
		return rectSet;
	}

	@Override
	public Rectangle bounds() {
		return getInstance().bounds();
	}

	@Override
	public RectSet part(String name) {
		return getInstance().part(name);
	}

	@Override
	public RectSet nestedPart(String name) {
		return getInstance().nestedPart(name);
	}
}
