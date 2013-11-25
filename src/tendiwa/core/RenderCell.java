package tendiwa.core;

public class RenderCell {
int x;
int y;
boolean visible;
short floor;
short wall;
RenderMiscellaneousCellContents contents;

public RenderCell(int x, int y, short floor, short wall) {
	this.x = x;
	this.y = y;
	this.floor = floor;
	this.wall = wall;
	visible = true;
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

public static int getY(int coord) {
	return coord % Tendiwa.getWorld().getHeight();
}

public static int getX(Integer coord) {
	return coord / Tendiwa.getWorld().getHeight();
}

public boolean hasWall() {
	return wall != WallType.NO_WALL_ID;
}
}
