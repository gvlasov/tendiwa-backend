package tests.painting;

import javax.swing.SwingUtilities;

import painting.TestCanvas;
import tendiwa.recsys.RandomRectangleSystem;
import tendiwa.recsys.RectangleSystem;

public class RectangleSystemDrawTest {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RectangleSystemDrawTest().testDraw();
            }
        });
	}
	public void testDraw() {
		RectangleSystem rs = new RandomRectangleSystem(0, 0, 1280, 1024, 3, 0);
		new TestCanvas().draw(rs);
	}
}
