package org.tendiwa.drawing;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.*;

import java.awt.Color;
import java.awt.Shape;
import java.util.function.Function;
import java.util.stream.Stream;

public interface DrawableInto {
	<T> void draw(T what, DrawingAlgorithm<? super T> how, BaseTestCanvas.Layer where);

	<T> void draw(T what, DrawingAlgorithm<? super T> how);

	void draw(Drawable drawable);

	default <T> void drawAll(Iterable<T> whats, DrawingAlgorithm<? super T> how) {
		for (T what : whats) {
			draw(what, how);
		}
	}

	default <T> void drawAll(Iterable<T> what, Function<T, Drawable> toDrawable) {
		for (T shape : what) {
			toDrawable.apply(shape).drawIn(this);
		}
	}

	default <T> void drawAll(Stream<T> what, Function<T, Drawable> toDrawable) {
		what.forEach(element -> toDrawable.apply(element).drawIn(this));
	}

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

	void drawRectangle2D(BasicRectangle2D r, Color color);

	void drawRasterLine(BasicCell p1, BasicCell p2, Color color);

	void drawLine(double startX, double startY, double endX, double endY, Color color);

	default void drawRasterLine(Segment2D line, Color color) {
		drawRasterLine(line.start().toCell(), line.end().toCell(), color);
	}

	default void drawCell(Cell cell, Color color) {
		drawCell(cell.x(), cell.y(), color);
	}

	void fillShape(Shape shape, Color color);

	void drawShape(Shape shape, Color color);

	void drawString(String text, double x, double y, Color color);

	int textWidth(String string);

	int textLineHeight();
}
