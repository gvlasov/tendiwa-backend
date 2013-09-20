package tests.painting;

import tendiwa.drawing.DrawingRectangle;
import tendiwa.drawing.TestCanvas;
import tendiwa.drawing.TestCanvasBuilder;
import tendiwa.geometry.Directions;
import tendiwa.geometry.EnhancedRectangle;

import java.awt.*;

public class RectangleSidePieceDemo {
    public static void main(String[] args) {
        TestCanvas canvas = new TestCanvasBuilder().setScale(3).setSize(1024,768).build();
        EnhancedRectangle r1 = new EnhancedRectangle(5, 5, 15, 5);
        EnhancedRectangle r2 = r1.getSideAsSidePiece(Directions.S).createRectangle(4);
        canvas.draw(r1);
        canvas.draw(r2, DrawingRectangle.withColor(Color.YELLOW));
        canvas.draw(r1.getSideAsSidePiece(Directions.S));
        canvas.draw(r2.getSideAsSidePiece(Directions.N));
    }
}
