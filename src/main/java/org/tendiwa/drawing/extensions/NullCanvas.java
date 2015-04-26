package org.tendiwa.drawing.extensions;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.*;

import java.awt.Color;
import java.awt.Shape;

public class NullCanvas implements Canvas {
	@Override
	public void draw(Drawable drawable) {

	}

	@Override
	public int getScale() {
		return 1;
	}

	@Override
	public void fillBackground(Color backgroundColor) {

	}

	@Override
	public Dimension size() {
		return null;
	}

	@Override
	public void clear() {

	}

	@Override
	public void drawCell(int x, int y, Color color) {

	}

	@Override
	public void drawRectangle(Rectangle r, Color color) {

	}

	@Override
	public void drawRectangle2D(Rectangle2D r, Color color) {

	}

	@Override
	public void drawRasterLine(Cell p1, Cell p2, Color color) {

	}

	@Override
	public void drawLine(double startX, double startY, double endX, double endY, Color color) {

	}

	@Override
	public void drawString(String text, Point2D start, Color color) {

	}

	@Override
	public int textWidth(String string) {
		return 0;
	}

	@Override
	public int textLineHeight() {
		return 0;
	}

	@Override
	public void drawSegment2D(Segment2D segment, Color color) {

	}

	@Override
	public void drawCircle(Circle circle, Color color) {

	}
}
