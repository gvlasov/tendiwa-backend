package org.tendiwa.drawing;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class DrawingModule extends AbstractModule {
@Override
protected void configure() {
	bind(TestCanvas.class)
		.annotatedWith(Names.named("default"))
		.toProvider(DefaultTestCanvasProvider.class)
		.in(Scopes.SINGLETON);
	bind(DefaultDrawingAlgorithms.class)
		.annotatedWith(Names.named("default"))
		.toProvider(DefaultDefaultDrawingAlgorithmsProvider.class)
		.in(Scopes.SINGLETON);

}
}
