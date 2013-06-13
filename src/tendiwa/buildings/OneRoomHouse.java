package tendiwa.buildings;

import tendiwa.core.Building;
import tendiwa.core.StaticData;
import tendiwa.core.meta.Side;
import tendiwa.core.terrain.settlements.BuildingPlace;

public class OneRoomHouse extends Building {
	public static final long serialVersionUID = 35681734L;
	public OneRoomHouse(BuildingPlace bp, Side side) {
		super(bp, side);
	}

	public void draw() {
		int wallWoorden = StaticData.getObjectType("wall_wooden").getId();
		
		getTerrainModifier(900);	
		buildBasis(wallWoorden);
		
		placeFrontDoor(getDoorSide());
	}
	@Override
	public boolean fitsToPlace(BuildingPlace place) {
		// TODO Auto-generated method stub
		return place.width > 6 || place.height > 6;
	}
}
