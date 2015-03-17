package org.tendiwa.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;

public class SuccessiveTuples {
	public static <T> void forEachLooped(Iterable<T> iterable, BiConsumer<T, T> consumer) {
		Iterator<T> iterator = iterable.iterator();
		T previous;
		try {
			previous = iterator.next();
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException(
				"Iterable must produce at least 2 elements before ending; it produced 0"
			);
		}
		T first = previous;
		if (!iterator.hasNext()) {
			throw new NoSuchElementException(
				"Iterable must produce at least 2 elements before ending; it produced 1"
			);
		}
		while (iterator.hasNext()) {
			T current = iterator.next();
			consumer.accept(previous, current);
			previous = current;
		}
		consumer.accept(previous, first);
	}

	public static <T> void forEach(Iterable<T> iterable, BiConsumer<T, T> consumer) {
		Iterator<T> iterator = iterable.iterator();
		T previous;
		try {
			previous = iterator.next();
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException(
				"Iterable must produce at least 2 elements before ending; it produced 0"
			);
		}
		T first = previous;
		if (!iterator.hasNext()) {
			throw new NoSuchElementException(
				"Iterable must produce at least 2 elements before ending; it produced 1"
			);
		}
		while (iterator.hasNext()) {
			T current = iterator.next();
			consumer.accept(previous, current);
			previous = current;
		}
	}

	public static <T> void forEachLooped(Iterable<T> iterable, TriConsumer<T> consumer) {
		Iterator<T> iterator = iterable.iterator();
		T prePrevious;
		try {
			prePrevious = iterator.next();
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException(
				"Iterable must produce at least 3 elements before ending; it produced 0"
			);
		}
		T first = prePrevious;
		T previous;
		try {
			previous = iterator.next();
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException(
				"Iterable must produce at least 3 elements before ending; it produced 1"
			);
		}
		T second = previous;
		if (!iterator.hasNext()) {
			throw new NoSuchElementException(
				"Iterable must produce at least 3 elements before ending; it produced 2"
			);
		}
		while (iterator.hasNext()) {
			T current = iterator.next();
			consumer.accept(prePrevious, previous, current);
			prePrevious = previous;
			previous = current;
		}
		consumer.accept(prePrevious, previous, first);
		consumer.accept(previous, first, second);
	}

	@FunctionalInterface
	public interface TriConsumer<T> {
		public void accept(T a, T b, T c);
	}
}
