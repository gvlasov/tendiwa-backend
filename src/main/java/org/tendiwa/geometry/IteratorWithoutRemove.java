package org.tendiwa.geometry;

import com.google.common.collect.UnmodifiableIterator;

import java.util.Iterator;

final class IteratorWithoutRemove<T> extends UnmodifiableIterator<T> {
	private final Iterator<T> iterator;

	IteratorWithoutRemove(Iterator<T> wrapped) {
		this.iterator = wrapped;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		return iterator.next();
	}
}
