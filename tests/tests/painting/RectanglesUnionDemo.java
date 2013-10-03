package tests.painting;

import tendiwa.drawing.DrawingRectangleSystem;
import tendiwa.geometry.RectangleSystem;

import java.awt.*;

import static tendiwa.geometry.DSL.*;
public class RectanglesUnionDemo {
public static void main(String[] args) {
	RectangleSystem rs = builder(1)
		.place(rectangle(10, 12), atPoint(20, 28))
		.place(rectangle(29, 41), unitedWith(LAST_RECTANGLE).fromSide(E).align(S))
		.place(rectangle(29, 41), near(LAST_RECTANGLE).fromSide(E).align(S))
		.done();
	canvas().draw(rs, DrawingRectangleSystem.neighborsUnionsAndRectangles(
		Color.RED,
		Color.BLUE,
		Color.BLACK,
		Color.DARK_GRAY,
		Color.GRAY,
		Color.LIGHT_GRAY
	));
}
}
