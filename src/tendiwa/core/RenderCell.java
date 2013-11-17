package tendiwa.core;

public class RenderCell {
int x;
int y;
boolean visible;
short terrain;

public RenderCell(int x, int y, short terrain) {
	this.x = x;
	this.y = y;
	this.terrain = terrain;
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

public short getTerrain() {
	return terrain;
}

public static int getY(int coord) {
	return coord % Tendiwa.getWorld().getHeight();
}

public static int getX(Integer coord) {
	return coord / Tendiwa.getWorld().getHeight();
}
}
