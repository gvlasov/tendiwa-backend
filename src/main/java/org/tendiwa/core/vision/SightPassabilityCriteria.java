package org.tendiwa.core.vision;

public interface SightPassabilityCriteria {
	/**
	 * Checks if with this criteria a Seer can see cell {endX:endY} from neighbor cells of that cells (i.e. cells from
	 * 8
	 * directions around that cell).
	 *
	 * @param endX
	 * 	X coordinate of a cell in world coordinates.
	 * @param endY
	 * 	Y coordinate of a cell in world coordinates.
	 * @return true if cell is visible from its neighbor, false otherwise. What neighbor is it being seen from is
	 * determined
	 * in this method's implementation.
	 */
	public boolean canSee(int endX, int endY);

}
