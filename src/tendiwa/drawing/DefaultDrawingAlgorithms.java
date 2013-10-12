package tendiwa.drawing;

import tendiwa.core.TerrainBasics;
import tendiwa.core.World;
import tendiwa.geometry.RectangleSidePiece;
import tendiwa.geometry.RectangleSystem;
import tendiwa.geometry.Segment;

import java.awt.*;
import java.util.HashMap;

/**
 * This static field class contains {@link DrawingAlgorithm}s that each
 * {@link TestCanvas} uses to draw objects if no other algorithm was specified
 * by API user for those object classes.
 * 
 * @author suseika
 * 
 */
final class DefaultDrawingAlgorithms {
	private DefaultDrawingAlgorithms() {

	}

	static HashMap<Class<?>, DrawingAlgorithm<?>> algorithms = new HashMap<Class<?>, DrawingAlgorithm<?>>();
	static {
		algorithms.put(Rectangle.class, DrawingRectangle.withColorLoop(Color.GRAY, Color.BLACK, Color.BLUE));
		algorithms.put(RectangleSystem.class, DrawingRectangleSystem
			.withColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW));
		algorithms.put(
			RectangleSidePiece.class,
			DrawingRectangleSidePiece.withColor(Color.MAGENTA));
		algorithms.put(Segment.class, DrawingSegment.withColor(Color.BLUE));
		algorithms.put(TerrainBasics.class, DrawingTerrain.defaultAlgorithm());
		algorithms.put(World.class, DrawingWorld.defaultAlgorithm());
	}
}
