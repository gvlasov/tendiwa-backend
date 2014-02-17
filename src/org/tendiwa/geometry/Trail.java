package org.tendiwa.geometry;

import org.tendiwa.core.Chunk;

import java.awt.Point;
import java.util.ArrayList;

public abstract class Trail {
protected ArrayList<Point> points = new ArrayList<>();
final int width;
public Trail(int width) {
	// TODO Auto-generated constructor stub
	this.width = width;
}
public Trail nextPoint(int x, int y) {
	points.add(new Point(x, y));
	return this;
}
public abstract void draw(Chunk terrain);
}
