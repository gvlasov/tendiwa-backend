package tendiwa.core;

public class RenderCell {
public final int x;
public final int y;
boolean visible;
short floor;
short wall;

public RenderCell(int x, int y, short floor, short wall) {
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

public short getFloor() {
	return floor;
}

public short getWall() {
	return wall;
}

public boolean hasWall() {
	return wall != WallType.NO_WALL_ID;
}
}
