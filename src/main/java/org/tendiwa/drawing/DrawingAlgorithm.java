package org.tendiwa.drawing;

import org.tendiwa.core.Chunk;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Rectangle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public abstract class DrawingAlgorithm<T> {
protected TestCanvas canvas;
private static final AffineTransform defaultTransform = new AffineTransform();

static {
	defaultTransform.setToScale(1, 1);
}

public abstract void draw(T shape);

protected void drawPoint(int x, int y, Color color) {
	if (canvas.scale == 1) {
		canvas.image.setRGB(x, y, color.getRGB());
	} else {
		canvas.graphics.setColor(color);
		canvas.graphics.fillRect(
			x * canvas.scale,
			y * canvas.scale,
			canvas.scale,
			canvas.scale);
	}
}

protected void drawRectangle(Rectangle r, Color color) {
	canvas.graphics.setColor(color);
	Graphics2D g2d = (Graphics2D) canvas.graphics;
	AffineTransform transform = new AffineTransform();
	transform.setToScale(canvas.scale, canvas.scale);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setTransform(transform);
	g2d.fill(r.toAwtRectangle());
	g2d.setTransform(defaultTransform);
//	canvas.graphics.fillRect(
//		r.getX() * canvas.scale,
//		r.getY() * canvas.scale,
//		r.getWidth() * canvas.scale,
//		r.getHeight() * canvas.scale);
}

protected int getCanvasWidth() {
	return canvas.width;
}

protected int getCanvasHeight() {
	return canvas.height;
}

void setCanvas(TestCanvas canvas) {
	this.canvas = canvas;
}

void setLayer(TestCanvas.Layer zIndex) {
	canvas.graphics = zIndex.graphics;
	canvas.image = zIndex.image;
}

protected void drawObject(Object shape) {
	canvas.draw(shape, canvas.currentLayer);
}

protected <T> void drawObject(T shape, DrawingAlgorithm<T> algorithm) {
	canvas.draw(shape, algorithm, canvas.currentLayer);
}

protected <T> void drawLine(Cell p1, Cell p2, Color color) {
	for (Cell coordinate : Chunk.vector(p1.x, p1.y, p2.x, p2.y)) {
		drawPoint(coordinate.x, coordinate.y, color);
	}
}

protected void drawLine(double startX, double startY, double endX, double endY, Color color) {
	drawShape(new Line2D.Double(startX, startY, endX, endY), color);
}

protected void fillShape(Shape shape, Color color) {
	Graphics2D g2d = (Graphics2D) canvas.graphics;
	canvas.graphics.setColor(color);
	AffineTransform transform = new AffineTransform();
	transform.setToScale(canvas.scale, canvas.scale);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setTransform(transform);
	g2d.fill(shape);
	g2d.setTransform(defaultTransform);
}
protected void drawShape(Shape shape, Color color) {
	Graphics2D g2d = (Graphics2D) canvas.graphics;
	canvas.graphics.setColor(color);
	AffineTransform transform = new AffineTransform();
	transform.setToScale(canvas.scale, canvas.scale);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setTransform(transform);
	g2d.draw(shape);
	g2d.setTransform(defaultTransform);
}

}
