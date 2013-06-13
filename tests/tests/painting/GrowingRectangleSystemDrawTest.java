package tests.painting;

import javax.swing.SwingUtilities;

import org.junit.Test;

import painting.TestCanvas;
import tendiwa.core.EnhancedRectangle;
import tendiwa.core.meta.Side;
import tendiwa.recsys.GrowingRectangleSystem;

public class GrowingRectangleSystemDrawTest {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GrowingRectangleSystemDrawTest().testZeroBorder();
            }
        });
	}
	@Test
	public void testZeroBorder() {
		EnhancedRectangle er = new EnhancedRectangle(100, 100, 30, 30);
		GrowingRectangleSystem grs = new GrowingRectangleSystem(0, er);
		grs.grow(er, Side.N, 100, 16, 0);
		grs.grow(er, Side.E, 12, 16, 0);
		grs.grow(er, Side.S, 12, 16, 0);
		grs.grow(er, Side.W, 12, 16, 0);
		new TestCanvas().draw(grs);
	}
}
