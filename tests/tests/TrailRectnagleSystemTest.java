package tests;

import org.junit.Test;

import tendiwa.core.meta.Range;
import tendiwa.geometry.TrailRectangleSystem;

public class TrailRectnagleSystemTest {

@Test
public void test() {
	TrailRectangleSystem.splitRandomLengthIntoRandomPieces(new Range(40, 50), new Range(10, 20));
}

}
