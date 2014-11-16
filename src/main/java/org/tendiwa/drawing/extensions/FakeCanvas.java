package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Rectangle;

import java.awt.Color;
import java.awt.Shape;
import java.util.Collection;

public class FakeCanvas implements DrawableInto {
	@Override
	public <T> void draw(T what, DrawingAlgorithm<? super T> how, TestCanvas.Layer where) {

	}

	@Override
	public <T> void draw(T what, DrawingAlgorithm<? super T> how) {

	}

	@Override
	public <T> void drawAll(Collection<T> what, DrawingAlgorithm<? super T> how) {

	}

	@Override
	public void fillBackground(Color backgroundColor) {

	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getScale() {
		return 0;
	}

	@Override
	public void drawCell(int x, int y, Color color) {

	}

	@Override
	public void drawRectangle(Rectangle r, Color color) {

	}

	@Override
	public void drawRasterLine(Cell p1, Cell p2, Color color) {

	}

	@Override
	public void drawLine(double startX, double startY, double endX, double endY, Color color) {

	}

	@Override
	public void fillShape(Shape shape, Color color) {

	}

	@Override
	public void drawShape(Shape shape, Color color) {

	}

	@Override
	public void drawString(String text, double x, double y, Color color) {

	}
}
