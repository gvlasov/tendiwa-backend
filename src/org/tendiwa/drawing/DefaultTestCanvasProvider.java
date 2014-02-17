package org.tendiwa.drawing;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import java.io.IOException;

public class DefaultTestCanvasProvider implements Provider<TestCanvas> {
private final DefaultDrawingAlgorithms algorithms;

@Inject
DefaultTestCanvasProvider(
	@Named("default") DefaultDrawingAlgorithms algorithms
) {
	this.algorithms = algorithms;
}

@Override
public TestCanvas get() {
	try {
		return new TestCanvas(3, 1024, 800, algorithms, true, 1);
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
}
}
