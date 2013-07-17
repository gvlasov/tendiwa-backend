package tests.examples;

import org.junit.Test;

import painting.StdTerrainViewer;
import tendiwa.buildings.House;
import tendiwa.core.HorizontalPlane;
import tendiwa.core.Location;
import tendiwa.core.terrain.settlements.BuildingPlace;
import tendiwa.geometry.Directions;
import tendiwa.locationtypes.Forest;

public class AsciiTerrainViewerExample {

@Test
public void test() {
	Location forest = new Forest(new HorizontalPlane(), 0, 0, 60, 60);
	House house = new House(forest, new BuildingPlace(3, 3, 22, 22), Directions.E);
	forest.placeBuilding(house);
	new StdTerrainViewer(forest).printNumbers()
		.addRectangleSystem(house.getRs())
		.print();

}

}
