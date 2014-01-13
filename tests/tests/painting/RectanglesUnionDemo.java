package tests.painting;

import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.DrawingRectangle;
import org.tendiwa.drawing.DrawingRectangleSystem;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.core.EnhancedRectangle;
import org.tendiwa.core.RectangleSystemBuilder;
import org.tendiwa.core.RectanglesJunction;

import java.awt.*;

import static org.tendiwa.core.DSL.*;

public class RectanglesUnionDemo {
public static void main(String[] args) {
	RectangleSystemBuilder builder = builder(10)
		.place("start", rectangle(10, 12), atPoint(20, 28))
		.place(rectangle(29, 41), unitedWith(LAST_RECTANGLE).fromSide(E).align(S))
		.place(rectangle(29, 41), near(LAST_RECTANGLE).fromSide(E).align(S))
		.place(rectangle(30, 20), unitedWith(FIRST_RECTANGLE).fromSide(S).inMiddle())
		.place(rectangle(10, 15), near(LAST_RECTANGLE).fromSide(E).align(N));
	TestCanvas canvas = canvas();
	canvas.draw(builder.getByIndex(2), DrawingRectangle.withColor(Color.RED));
	canvas.draw(builder.getByIndex(3), DrawingRectangle.withColor(Color.YELLOW));
	SimpleGraph<EnhancedRectangle,RectanglesJunction> path = path(builder)
		.link("start").with(1).width(3).shift(2)
		.link(1).with(2).width(2).shift(0)
		.link(3).with(LAST_RECTANGLE).width(3).shift(1)
		.build();
	canvas.draw(builder.done(), DrawingRectangleSystem.neighborsUnionsAndRectangles(
		Color.RED,
		Color.BLUE,
		Color.BLACK,
		Color.DARK_GRAY,
		Color.GRAY,
		Color.LIGHT_GRAY
	));
	canvas.draw(path);
}
}
