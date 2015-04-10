package org.tendiwa.drawing;

import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.thoughtworks.xstream.converters.reflection.CGLIBEnhancedConverter;

public class DefaultTestCanvasProvider implements Provider<DrawableInto> {
	@Override
	public DrawableInto get() {
		return new TestCanvas(1, 800, 600);
	}
}
