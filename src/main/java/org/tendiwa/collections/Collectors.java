package org.tendiwa.collections;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.LinkedHashSet;
import java.util.stream.Collector;

public final class Collectors {

	public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet() {
		return new ImmutableSetCollector<>();
	}

	public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList() {
		return new ImmutableListCollector<>();
	}

	public static <T> Collector<T, ?, LinkedHashSet<T>> toLinkedHashSet() {
		return java.util.stream.Collectors.toCollection(LinkedHashSet::new);
	}
}
