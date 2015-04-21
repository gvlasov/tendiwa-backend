package org.tendiwa.collections;

import java.util.Optional;

public interface Maybe<T> {
	T get();

	boolean isPresent();

	default Optional<T> asOptional() {
		return isPresent() ?
			Optional.of(get()) :
			Optional.empty();
	}
}
