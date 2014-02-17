package org.tendiwa.drawing;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.tendiwa.core.*;
import org.tendiwa.geometry.EnhancedRectangle;
import org.tendiwa.geometry.RectangleSidePiece;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.Segment;

import java.awt.*;

public class DefaultDefaultDrawingAlgorithmsProvider implements Provider<DefaultDrawingAlgorithms> {
private final DefaultDrawingAlgorithms defaultDrawingAlgorithms;

@Inject
DefaultDefaultDrawingAlgorithmsProvider(
	DefaultDrawingAlgorithms defaultDrawingAlgorithms
) {

	this.defaultDrawingAlgorithms = defaultDrawingAlgorithms;
}

@Override
public DefaultDrawingAlgorithms get() {
	defaultDrawingAlgorithms.register(EnhancedRectangle.class, DrawingRectangle.withColorLoop(Color.GRAY, Color.BLACK, Color.BLUE));
	defaultDrawingAlgorithms.register(RectangleSystem.class, DrawingRectangleSystem
		.withColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW));
	defaultDrawingAlgorithms.register(
		RectangleSidePiece.class,
		DrawingRectangleSidePiece.withColor(Color.MAGENTA));
	defaultDrawingAlgorithms.register(Segment.class, DrawingSegment.withColor(Color.BLUE));
	defaultDrawingAlgorithms.register(Chunk.class, DrawingTerrain.defaultAlgorithm());
//	defaultDrawingAlgorithms.register(World.class, DrawingWorld.level(0));

	return defaultDrawingAlgorithms;
}
}
