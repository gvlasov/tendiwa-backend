package tendiwa.buildings;

import tendiwa.core.Building;
import tendiwa.core.StaticData;
import tendiwa.core.terrain.settlements.BuildingPlace;
import tendiwa.geometry.*;

public class Inn extends Building {
public static final long serialVersionUID = 11672547L;

public Inn(BuildingPlace bp, CardinalDirection side) {
	super(bp, side);
}

public void draw() {
	int wallGreyStone = StaticData.getObjectType("wall_gray_stone").getId();

	RectangleSystem crs = new RectangleSystem(1);
		/* BASIS */
	// Lobby
	Orientation dir;
	CardinalDirection side = Directions.S;
	int lobbyWidth = 5;
	if (side == Directions.N || side == Directions.S) {
		dir = Orientation.HORIZONTAL;
	} else {
		dir = Orientation.VERTICAL;
	}
	EnhancedRectangle rightRooms = crs.addRectangle(new EnhancedRectangle(x, y, width, height));
	// Separate middle rectangle (lobby) and left rectangle, get left rooms
	EnhancedRectangle leftRooms = crs.cutRectangleFromSide(rightRooms, side.clockwiseQuarter(), ((((dir.isHorizontal()) ? width
			: height) - lobbyWidth) / 2 - 1));
	// Separate middle rectangle (lobby) and right rectangle, get lobby
	EnhancedRectangle lobby = crs.cutRectangleFromSide(rightRooms, side.clockwiseQuarter(), lobbyWidth);

	// Separate rectangle above lobby
	EnhancedRectangle aboveLobby = crs.cutRectangleFromSide(lobby, side.opposite(), 4);
	// Left hall
	EnhancedRectangle leftHall = crs.cutRectangleFromSide(leftRooms, side.counterClockwiseQuarter(), 2);
	// Right hall
	EnhancedRectangle rightHall = crs.cutRectangleFromSide(rightRooms, side.clockwiseQuarter(), 2);
	// 1 - left rooms, 4 - left hall, 3 - above middle, 2 - middle, 5 -
	// right hall, 0 - right rooms
	// crs.cutRectangleFromSide(rightRoomsId, side, 1);
	// int firstSideRoom = 6;
	// Place left rooms and link them with left hall
	while (dir.isHorizontal() && leftRooms.height > 5 || dir.isVertical() && leftRooms.width > 5) {
		crs.cutRectangleFromSide(leftRooms, side, 4);
	}
	// Place right rooms and link them with right hall
	while (dir.isHorizontal() && rightRooms.height > 5 || dir.isVertical() && rightRooms.width > 5) {
		crs.cutRectangleFromSide(rightRooms, side, 4);
	}
	// Link last room

	terrainModifier = settlement.getTerrainModifier(crs);
	terrainModifier.getRectangleSystem().findOuterRectangles();
	lobby.stretch(side, -1);
	buildBasis(wallGreyStone);

	placeFrontDoor(lobby, side);

		/* CONTENTS */
	// Rectangle lobbyRec = terrainModifier.content.get(lobbyId);
	// for (int i=firstSideRoom, size = crs.rectangles.size();i<size;i++) {
	// ArrayList<Coordinate> cells =
	// getCellsNearWalls(crs.rectangles.get(i));
	// }
}

public boolean fitsToPlace(BuildingPlace place) {
	return (place.width > 23 || place.height > 23);
}
}
