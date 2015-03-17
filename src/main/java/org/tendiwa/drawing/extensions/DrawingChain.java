package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Chain2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;

public class DrawingChain {
	public static DrawingAlgorithm<Chain2D> withColorThin(Color color) {
		return (chain, canvas) -> {
			DrawingAlgorithm<Segment2D> drawingSegment = DrawingSegment2D.withColorThin(color);
			chain.asSegmentStream()
				.forEach(segment -> canvas.draw(segment, drawingSegment));
		};
	}
}
