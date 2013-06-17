package tendiwa.locationtypes;

import tendiwa.core.HorizontalPlane;
import tendiwa.core.StaticData;
import tendiwa.core.terrain.settlements.Settlement;

public class Village extends Settlement {
	public Village(HorizontalPlane plane, int x, int y, int width, int height) {
		super(plane, x, y, width, height);
		int floorGrass = StaticData.getFloorType("grass").getId();
		fillWithCells(floorGrass, StaticData.VOID);
		createRandomRoadSystem();
		roadSystem.createRoad(width/2,0,width/2,height-1);
		roadSystem.drawRoads();
		quarterSystem.build(roadSystem.getReferencePoints());
//		for (BuildingPlace place : quarterSystem.buildingPlaces) {
//			placeBuilding(place, Inn.class);
//		}
	}
}
