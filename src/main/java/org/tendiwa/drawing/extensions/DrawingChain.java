package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Colors;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.List;

public class DrawingChain {
	public static DrawingAlgorithm<List<Point2D>> withColor(Color color) {
		return (chain, canvas) -> {
			Point2D previous = null;
			float[] clr = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			for (Point2D vertex : chain) {
				if (previous == null) {
					previous = vertex;
					continue;
				}
				clr[0] = (float) (clr[0] + 0.25) % 1;
				Color hsb = Color.getHSBColor(clr[0], clr[1], clr[2]);
				canvas.drawLine(
					previous.x,
					previous.y,
					vertex.x,
					vertex.y,
//					hsb
					color
				);
				previous = vertex;

			}
		};
	}
}
