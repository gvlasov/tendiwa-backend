package tests.painting;

import tendiwa.core.HorizontalPlane;
import tendiwa.drawing.TestCanvas;
import tendiwa.geometry.DSL;
import tendiwa.geometry.InterrectangularPath;
import tendiwa.locationtypes.BuildingTest;

public class BuildingsDemo {
public static void main(String[] args) {
	TestCanvas canvas = DSL.canvas();
	HorizontalPlane plane = new HorizontalPlane();
	plane.generateLocation(0, 0, 400, 300, BuildingTest.class);
	canvas.draw(plane);
}
}
