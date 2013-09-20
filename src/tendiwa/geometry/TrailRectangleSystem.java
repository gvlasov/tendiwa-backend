package tendiwa.geometry;

import tendiwa.core.meta.Range;

import java.awt.*;
import java.util.ArrayList;

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
