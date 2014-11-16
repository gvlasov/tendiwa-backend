package org.tendiwa.drawing;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.awt.Shape;
import java.util.Collection;

public interface DrawableInto {
	<T> void draw(T what, DrawingAlgorithm<? super T> how, BaseTestCanvas.Layer where);

	<T> void draw(T what, DrawingAlgorithm<? super T> how);

	<T> void drawAll(Collection<T> what, DrawingAlgorithm<? super T> how);

	void fillBackground(Color backgroundColor);

	/**
	 * Width in pixels.
	 *
	 * @return
	 */
	int getWidth();

	/**
	 * Height in pixels.
	 *
	 * @return
	 */
	int getHeight();

	/**
	 * Size of a cell in pixels.
	 *
	 * @return
	 */
	int getScale();

	void drawCell(int x, int y, Color color);

	void drawRectangle(Rectangle r, Color color);

	void drawRasterLine(Cell p1, Cell p2, Color color);

	void drawLine(double startX, double startY, double endX, double endY, Color color);

	default void drawRasterLine(Segment2D line, Color color) {
		drawRasterLine(line.start.toCell(), line.end.toCell(), color);
	}

	default void drawCell(Cell cell, Color color) {
		drawCell(cell.x, cell.y, color);
	}

	void fillShape(Shape shape, Color color);

	void drawShape(Shape shape, Color color);

	void drawString(String text, double x, double y, Color color);
}
