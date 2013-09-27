package tendiwa.locationtypes;

import tendiwa.buildings.House;
import tendiwa.core.HorizontalPlane;
import tendiwa.core.Location;
import tendiwa.core.StaticData;
import tendiwa.core.TerrainModifier;
import tendiwa.core.terrain.settlements.BuildingPlace;
import tendiwa.geometry.Directions;
import tendiwa.geometry.RectangleSystem;

public class Forest extends Location {
	int wall = StaticData.getObjectType("wall_grey_stone").getId();
	int grass = StaticData.getFloorType("grass").getId();
	int water = StaticData.getFloorType("water").getId();

	public Forest(HorizontalPlane plane, int x, int y, int width, int height) {
		super(plane, x, y, width, height, "Forest");
		House house = new House(this, new BuildingPlace(3, 3, 22, 22), Directions.E);
		placeBuilding(house);
	}
	private void recSysTest() {
		RectangleSystem rs = new RectangleSystem(3);
		// RectangleSystem rs = new RandomRectangleSystem(2, 2, 55, 22, 3, 2);
		TerrainModifier tm = new TerrainModifier(this, rs);
		tm.fillContents(ELEMENT_FLOOR, water);
		tm.drawInnerBorders(ELEMENT_OBJECT, wall);
		tm.drawOuterBorders(ELEMENT_OBJECT, wall);
	}
}
