package tendiwa.core;

public class RenderCell {
public final int x;
public final int y;
boolean visible;
FloorType floor;
WallType wall;

public RenderCell(int x, int y, FloorType floor, WallType wall) {
	this.x = x;
	this.y = y;
	this.floor = floor;
	this.wall = wall;
	visible = true;
}

@Override
public String toString() {
	return "RenderCell{" +
		"x=" + x +
		", y=" + y +
		", visible=" + visible +
		", floor=" + floor +
		", wall=" + wall +
		'}';
}

public static int getY(int coord) {
	return coord % Tendiwa.getWorld().getHeight();
}

public static int getX(Integer coord) {
	return coord / Tendiwa.getWorld().getHeight();
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

public WallType getWall() {
	return wall;
}

public boolean hasWall() {
	return wall != null;
}
}
