package tests.painting;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.core.Directions;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.DrawingRectangle;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Rectangle;

import java.awt.*;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class RectangleSidePieceDemo {
@Inject
@Named("default")
TestCanvas canvas;

@Test
public void draw() {
	org.tendiwa.geometry.Rectangle r1 = new Rectangle(5, 5, 15, 5);
	Rectangle r2 = r1.getSideAsSidePiece(Directions.S).createRectangle(4);
	canvas.draw(r1);
	canvas.draw(r2, DrawingRectangle.withColor(Color.YELLOW));
	canvas.draw(r1.getSideAsSidePiece(Directions.S));
	canvas.draw(r2.getSideAsSidePiece(Directions.N));
}
}
