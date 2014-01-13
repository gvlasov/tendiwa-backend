package org.tendiwa.core;

public class RenderCell {
public final int x;
public final int y;
boolean visible;
FloorType floor;
GameObject object;

public RenderCell(int x, int y, FloorType floor, GameObject object) {
	assert floor != null;
	this.x = x;
	this.y = y;
	this.floor = floor;
	this.object = object;
	visible = true;
}

public static int getY(int coord) {
	return coord % Tendiwa.getWorld().getHeight();
}

public static int getX(Integer coord) {
	return coord / Tendiwa.getWorld().getHeight();
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
	result = Tendiwa.getWorldHeight() * result + y;
	return result;
}
}
