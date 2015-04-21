package org.tendiwa.math;

import java.util.stream.IntStream;

public interface Permutation {
	int[] generate();

	default IntStream stream() {
		return IntStream.of(generate());
	}

}
