package org.tendiwa.demos;

import com.google.inject.Inject;
import org.tendiwa.core.Directions;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingRectangleSidePiece;
import org.tendiwa.geometry.Rectangle;

import java.awt.Color;

public class RectangleSidePieceDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(RectangleSidePieceDemo.class);
	}

	public void run() {
		Rectangle r1 = new Rectangle(5, 5, 15, 5);
		Rectangle r2 = r1.getSideAsSidePiece(Directions.S).createRectangle(4);
		canvas.drawRectangle(r1, Color.RED);
		canvas.drawRectangle(r2, Color.YELLOW);
		canvas.draw(r1.getSideAsSidePiece(Directions.S), DrawingRectangleSidePiece.withColor(Color.BLUE));
		canvas.draw(r2.getSideAsSidePiece(Directions.N), DrawingRectangleSidePiece.withColor(Color.GREEN));
	}
}
