package org.tendiwa.drawing;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Geometry;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.core.Chunk;
import org.tendiwa.geometry.*;

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
	defaultDrawingAlgorithms.register(org.tendiwa.geometry.Rectangle.class, DrawingRectangle.withColorLoop(Color.GRAY, Color.BLACK, Color.BLUE));
	defaultDrawingAlgorithms.register(RectangleSystem.class, DrawingRectangleSystem
		.withColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW));
	defaultDrawingAlgorithms.register(
		RectangleSidePiece.class,
		DrawingRectangleSidePiece.withColor(Color.MAGENTA));
	defaultDrawingAlgorithms.register(Segment.class, DrawingSegment.withColor(Color.BLUE));
	defaultDrawingAlgorithms.register(Chunk.class, DrawingTerrain.defaultAlgorithm());
	defaultDrawingAlgorithms.register(Geometry.class, new DrawingAlgorithm<Geometry>() {
		@Override
		public void draw(Geometry shape) {
			this.fillShape(new ShapeWriter().toShape(shape), Color.RED);
		}
	});
//	defaultDrawingAlgorithms.register(World.class, DrawingWorld.level(0));
    defaultDrawingAlgorithms.register(Point2D.class, DrawingPoint.withColorAndSize(Color.RED, 3));
    defaultDrawingAlgorithms.register(Line2D.class, DrawingLine.withColor(Color.RED));

	return defaultDrawingAlgorithms;
}
}
