package org.tendiwa.drawing;

import com.google.inject.Provider;

public class DefaultTestCanvasProvider implements Provider<Canvas> {
	@Override
	public Canvas get() {
		return new TestCanvas(1, 800, 600);
	}
}
