package org.tendiwa.drawing;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This static field class contains {@link DrawingAlgorithm}s that each {@link TestCanvas} uses to raw objects if no
 * other algorithm was specified by API user for those object classes.
 *
 * @author suseika
 */
@Singleton
public final class DefaultDrawingAlgorithms implements Iterable<Class<?>> {
private final HashMap<Class<?>, DrawingAlgorithm<?>> algorithms = new HashMap<>();

@Inject
public DefaultDrawingAlgorithms() {

}

public <T> void register(Class<T> cls, DrawingAlgorithm<? super T> algorithm) {
	algorithms.put(cls, algorithm);
}

@Override
public Iterator<Class<?>> iterator() {
	return algorithms.keySet().iterator();
}
public DrawingAlgorithm get(Class<?> cls) {
	return algorithms.get(cls);
}
}
