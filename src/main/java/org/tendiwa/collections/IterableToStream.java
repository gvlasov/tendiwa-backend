package org.tendiwa.collections;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IterableToStream {
	public static <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(
				iterable.iterator(),
				Spliterator.ORDERED
			),
			false
		);
	}
	public static <T> Stream<T> stream(Iterator<T> iterator) {
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(
				iterator,
				Spliterator.ORDERED
			),
			false
		);
	}
}
