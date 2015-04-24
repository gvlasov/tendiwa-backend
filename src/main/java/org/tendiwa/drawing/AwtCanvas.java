package org.tendiwa.drawing;

import java.awt.Color;
import java.awt.Shape;
import java.awt.image.BufferedImage;

public interface AwtCanvas extends Canvas {
	BufferedImage getImage();

	void fillShape(Shape shape, Color color);

	void drawShape(Shape shape, Color color);
}
