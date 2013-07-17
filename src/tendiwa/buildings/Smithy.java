package tendiwa.buildings;

import tendiwa.core.Building;
import tendiwa.core.StaticData;
import tendiwa.core.terrain.settlements.BuildingPlace;
import tendiwa.geometry.CardinalDirection;
import tendiwa.geometry.RectangleArea;
import tendiwa.geometry.RectangleSystem;

public class Smithy extends Building {
	protected Smithy(BuildingPlace bp, CardinalDirection side) {
		super(bp, side);
	}

	public static final long serialVersionUID = 568456832L;

	public void draw() {
		int wallGreyStone = StaticData.getObjectType("wall_gray_stone").getId();

		RectangleSystem crs = new RectangleSystem(1);
		CardinalDirection side = CardinalDirection.S;
		RectangleArea initialRec = crs.addRectangleArea(x, y, width, height);
		RectangleArea r1 = crs.cutRectangleFromSide(initialRec, side, 5);
		RectangleArea r2 = crs.cutRectangleFromSide(r1, side.clockwiseQuarter(), 5);
		crs.excludeRectangle(r2);
		terrainModifier = settlement.getTerrainModifier(crs);
		buildBasis(wallGreyStone);
	}

	@Override
	public boolean fitsToPlace(BuildingPlace place) {
		// TODO Auto-generated method stub
		return true;
	}
}
