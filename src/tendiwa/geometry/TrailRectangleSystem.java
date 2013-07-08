package tendiwa.geometry;

import java.awt.Point;
import java.util.ArrayList;

import tendiwa.core.meta.Range;

public class TrailRectangleSystem extends RectangleSystem {

	protected ArrayList<Point> points = new ArrayList<Point>();
	protected Range sizeRange;
	public TrailRectangleSystem(int borderWidth, Point startPoint, Range sizeRange) {
		super(borderWidth);
		points.add(startPoint);
		this.sizeRange = sizeRange;
	}
	public TrailRectangleSystem(int borderWidth, Point startPoint, int sizeRange) {
		this(borderWidth, startPoint, new Range(sizeRange, sizeRange));
	}
	public TrailRectangleSystem nextPoint(Point point) {
		points.add(point);
		return this;
	}

}
