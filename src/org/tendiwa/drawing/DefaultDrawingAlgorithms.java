package org.tendiwa.drawing;

import java.util.HashMap;

/**
 * This static field class contains {@link DrawingAlgorithm}s that each {@link TestCanvas} uses to raw objects if no
 * other algorithm was specified by API user for those object classes.
 *
 * @author suseika
 */
public final class DefaultDrawingAlgorithms {
static HashMap<Class<?>, DrawingAlgorithm<?>> algorithms = new HashMap<>();

private DefaultDrawingAlgorithms() {

}

public static <T> void register(Class<T> cls, DrawingAlgorithm<? super T> algorithm) {
	algorithms.put(cls, algorithm);
}
}
