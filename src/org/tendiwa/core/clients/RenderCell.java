package org.tendiwa.core.clients;

import org.tendiwa.core.FloorType;
import org.tendiwa.core.GameObject;
import org.tendiwa.core.WallType;
import org.tendiwa.core.World;

/**
 * Holds contents and visibility of a particular cell. Instances of this class are sent from backend to a frontend. Once
 * frontend receives a RenderCell, it can alter its contents, but backend will never alter RenderCells.
 */
public class RenderCell {
public final int x;
public final int y;
private final int worldHeight;
boolean visible;
FloorType floor;
GameObject object;

/**
 * @param x
 * @param y
 * @param floor
 * @param object
 */
public RenderCell(World world, int x, int y, FloorType floor, GameObject object) {
	assert floor != null;
	this.x = x;
	this.y = y;
	this.floor = floor;
	this.object = object;
	visible = true;
	this.worldHeight = world.getHeight();

}

@Override
public String toString() {
	return "RenderCell{" +
		"x=" + x +
		", y=" + y +
		", visible=" + visible +
		", floor=" + floor +
		", object=" + object +
		'}';
}

public boolean isVisible() {
	return visible;
}

public void setVisible(boolean visible) {
	this.visible = visible;
}

public int getX() {
	return x;
}

public int getY() {
	return y;
}

public FloorType getFloor() {
	return floor;
}

public GameObject getObject() {
	return object;
}

public boolean hasWall() {
	return object != null && object instanceof WallType;
}

@Override
public boolean equals(Object o) {
	if (this == o) return true;
	if (o == null || getClass() != o.getClass()) return false;

	RenderCell that = (RenderCell) o;

	if (x != that.x) return false;
	if (y != that.y) return false;

	return true;
}

@Override
public int hashCode() {
	int result = x;
	result = worldHeight * result + y;
	return result;
}
}
