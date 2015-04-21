package org.tendiwa.core.worlds;

import org.tendiwa.core.TypePlaceableInCell;
import org.tendiwa.geometry.BasicCell;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class RandomContent<T extends TypePlaceableInCell> implements Function<BasicCell, T> {
	private final Random random;
	private final BiFunction<BasicCell, Random, T> function;

	RandomContent(Random random, BiFunction<BasicCell, Random, T> function) {
		this.function = function;
		this.random = new Random(random.nextInt());
	}

	@Override
	public T apply(BasicCell cell) {
		return function.apply(cell, random);
	}
}
