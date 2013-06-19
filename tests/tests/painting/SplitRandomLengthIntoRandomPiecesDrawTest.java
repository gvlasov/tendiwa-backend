package tests.painting;

import painting.TestCanvas;
import tendiwa.core.meta.Range;
import tendiwa.geometry.TrailRectangleSystem;

public class SplitRandomLengthIntoRandomPiecesDrawTest extends TestCanvas {
public static void main(String[] args) {
	visualize();
}
public void paint() {
	subclass = this.getClass();
	setScale(2);
	for (int i=0; i<40; i++) {
		int[] values = TrailRectangleSystem.splitRandomLengthIntoRandomPieces(
			new Range(14, 22), new Range(5, 8)
		);
		draw(values);
	}
}
}
