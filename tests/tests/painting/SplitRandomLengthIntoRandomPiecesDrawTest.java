package tests.painting;

import painting.TestCanvas;
import tendiwa.core.meta.Range;
import tendiwa.geometry.TrailRectangleSystem;

public class SplitRandomLengthIntoRandomPiecesDrawTest extends TestCanvas {

public void paint() {
	setScale(2);
	for (int i=0; i<20; i++) {
		int[] values = TrailRectangleSystem.splitRandomLengthIntoRandomPieces(
			new Range(400, 500), new Range(10, 40)
		);
		draw(values);
	}
}
}
