package org.tendiwa.drawing.extensions;

import java.awt.Color;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.OrthoCellSegment;

public class DrawingSegment {
	private DrawingSegment() {
	}

	public static DrawingAlgorithm<OrthoCellSegment> withColor(final Color color) {
		return (segment, canvas) -> {
			for (BasicCell point : segment) {
				canvas.drawCell(point.x(), point.y(), color);
			}

		};
	}

}
