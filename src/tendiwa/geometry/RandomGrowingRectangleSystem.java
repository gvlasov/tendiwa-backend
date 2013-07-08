package tendiwa.geometry;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

import tendiwa.core.Segment;
import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Range;
import tendiwa.core.meta.Side;

public class RandomGrowingRectangleSystem extends GrowingRectangleSystem {
	private int amountOfRectangles;
	private int rectangleWidth;
	/**
	 * A front #x is a set of rectangles that were created as neighbours of
	 * rectangles of a previous front #x-1, with a front #1 being solely the
	 * initial rectangle.
	 */
	private ArrayList<HashSet<RectangleArea>> rectanglesFront = new ArrayList<HashSet<RectangleArea>>();
	/**
	 * 
	 */
	private HashSet<RectangleArea> notFullyOcupiedRectanglesOnCurrentFront = new HashSet<RectangleArea>();
	private HashSet<RectangleArea> fullyOccupiedRectangles = new HashSet<RectangleArea>();
	private int lastFullyOccupiedFront = 0;
	public RandomGrowingRectangleSystem(int borderWidth, Range possibleRectangleWidth, int amountOfRectangles, Point startingPoint) {
		super(0);
		HashSet<RectangleArea> initialFront = new HashSet<RectangleArea>();
		RectangleArea initialRectangle = new RectangleArea(
			EnhancedRectangle.rectangleByCenterPoint(
				startingPoint, 
				Chance.rand(possibleRectangleWidth), 
				Chance.rand(possibleRectangleWidth)
			)
		);
		initialFront.add(initialRectangle);
		notFullyOcupiedRectanglesOnCurrentFront = new HashSet<RectangleArea>();
		notFullyOcupiedRectanglesOnCurrentFront.add(initialRectangle);
		rectanglesFront.add(initialFront);
		for (int i=0; i<amountOfRectangles; i++) {
			addRandomlyNewRectangle();
		}
	}
	public void addRandomlyNewRectangle() {
		for (RectangleArea r : notFullyOcupiedRectanglesOnCurrentFront) {
			HashSet<Segment> freeSegments = new HashSet<Segment>();
			for (Side side : Side.EACH_CARDINAL_SIDE) {
				freeSegments.addAll(getSegmentsFreeFromNeighbors(r, side));
			}
		}
	}
	public void setBorderWidth(int width) {
		this.borderWidth = borderWidth;
	}
	public void setAmountOfRectnagles(int amount) {
		this.amountOfRectangles = amount;
	}
	public void setRectangleWidth(int width) {
		this.rectangleWidth = width;
	}
	public void build() {
		
	}
}
