package org.tendiwa.drawing;

import java.awt.Color;

import org.tendiwa.core.Cell;
import org.tendiwa.geometry.Segment;

public class DrawingSegment {
	private DrawingSegment() {
	}
	public static DrawingAlgorithm<Segment> withColor(final Color color) {
		return new DrawingAlgorithm<Segment>() {
			
			@Override
			public void draw(Segment segment) {
				for (Cell point : segment) {
					drawPoint(point.getX(), point.getY(), color);
				}
				
			}
		};
	}

}
