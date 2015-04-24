package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Colors;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.RecTree;

final class DrawingRecTree {
	public static DrawingAlgorithm<RecTree> withDifferentColors(Colors... colors) {
		return new DrawingAlgorithm<RecTree>() {
			@Override
			public void draw(RecTree what, Canvas canvas) {

			}
		};
	}
}
