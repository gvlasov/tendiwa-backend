package org.tendiwa.collections;

import com.google.common.collect.ImmutableSet;

import java.util.stream.Collector;

public final class Collectors {

	public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet() {
		return new ImmutableSetCollector<>();
	}
}
