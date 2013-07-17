package tendiwa.buildings;

import java.awt.Rectangle;
import java.util.ArrayList;

import tendiwa.core.Building;
import tendiwa.core.CellCollection;
import tendiwa.core.StaticData;
import tendiwa.core.TerrainBasics;
import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Coordinate;
import tendiwa.core.terrain.settlements.BuildingPlace;
import tendiwa.geometry.CardinalDirection;
import tendiwa.geometry.EnhancedRectangle;

public class Crypt extends Building {
	public Crypt(BuildingPlace bp, CardinalDirection side) {
		super(bp, side);
		// TODO Auto-generated constructor stub
	}

	public static final long serialVersionUID = 836362727L;
	public Coordinate stairsCoord;

	public void draw() {
		int wallGreyStone = StaticData.getObjectType("wall_gray_stone").getId();
		int floorStone = StaticData.getFloorType("stone").getId();
		int objStatueGargoyle = StaticData.getObjectType("statue_gargoyle").getId();
		int objStairsDown = StaticData.getObjectType("stairs_down").getId();
		buildBasis(wallGreyStone);
		settlement.square(x, y, width, height, TerrainBasics.ELEMENT_FLOOR, floorStone, true);
		ArrayList<Rectangle> roomsValues = new ArrayList<Rectangle>(rooms);
		for (Rectangle r : rooms) {
			ArrayList<Coordinate> doorCells = getCellsNearDoors(r);
			int amountOfStatuePairs = Chance.rand(0, (r.width + r.height) - 4);
			for (int i = 0; i < amountOfStatuePairs; i++) {
				if (Chance.roll(50)) {
					int dy = Chance.rand(0, r.height - 1);
					settlement.setObject(r.x, r.y + dy, objStatueGargoyle);
					settlement.setObject(r.x + r.width - 1, r.y + dy, objStatueGargoyle);
				} else {
					int dx = Chance.rand(0, r.width - 1);
					settlement.setObject(r.x + dx, r.y, objStatueGargoyle);
					settlement.setObject(r.x + dx, r.y + r.height - 1, objStatueGargoyle);
				}
			}
			for (Coordinate c : doorCells) {
				settlement.setObject(c.x, c.y, StaticData.VOID);
			}
		}
		EnhancedRectangle stairsRec = new EnhancedRectangle(roomsValues.get(Chance.rand(0, roomsValues.size() - 1)));
		CellCollection stairsRoomCS = settlement.newCellCollection(stairsRec.getCells());
		stairsCoord = stairsRoomCS.setElementAndReport(TerrainBasics.ELEMENT_OBJECT, objStairsDown);
	}

	@Override
	public boolean fitsToPlace(BuildingPlace place) {
		return true;
	}
}
