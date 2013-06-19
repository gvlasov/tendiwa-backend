package tendiwa.geometry;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Range;
import tendiwa.core.meta.Side;

/**
 * A RectangleSystem that is defined by a set of points and that consists of
 * RectnalgeAreas forming lines between these points.
 * 
 * Each RectangleArea has varying width and height.
 * 
 * {@link TrailRectangleSystem} guarantees that it will contain RectangleAreas
 * that contain given points inside them, for each defined point. RectangleAreas
 * between points are meant to be rather chaotic and unpredictable.
 * 
 * @author suseika
 * 
 */
public class TrailRectangleSystem extends GrowingRectangleSystem {
protected Point lastPoint;
protected Range sizeRange;
final HashMap<Point, RectangleArea> pointsToRectangles = new HashMap<Point, RectangleArea>();

public TrailRectangleSystem(int borderWidth, Range sizeRange, Point start) {
	super(borderWidth);
	lastPoint = start;
	this.sizeRange = sizeRange;
}
public RectangleSystem buildToPoint(Point newPoint) {
	// You better not start trying to comprehend this code yourself. If you
	// really need to know how it works, better contact me — I'll happily 
	// explain its difficult moments (if I'll be able to remember how it works
	// myself).
	int dx = newPoint.x - lastPoint.x;
	int dy = newPoint.y - lastPoint.y;
	// First we work with distance on the greatest axis.
	int distanceBetweenPoints;
	Side toSecondRectangle; // Direction of new rectangles appearing on the
							// greatest axis
	Side toSecondRectangleSecodary; // Direction of new rectangles shifting on
									// the least axis
	boolean xIsGreater = dx >= dy;
	if (xIsGreater) {
		distanceBetweenPoints = Math.abs(dx);
		toSecondRectangle = Side.d2side(dx, 0);
		toSecondRectangleSecodary = Side.d2side(0, dy);
	} else {
		distanceBetweenPoints = Math.abs(dy);
		toSecondRectangle = Side.d2side(0, dy);
		toSecondRectangleSecodary = Side.d2side(dx, 0);
	}
	// First we assume that rectangles on the points are shifted away from each
	// other in such a way that distance between them is the greatest possible,
	// and the points are inside the rectangles, so only borders decrease the
	// distance left.
	int maxDistanceLeft = distanceBetweenPoints - borderWidth - 1;
	int maxNumOfRecsBetween = maxDistanceLeft / sizeRange.min;
	int minNumOfRecsBetween = maxDistanceLeft / sizeRange.max;
	if (maxNumOfRecsBetween > 1 && minNumOfRecsBetween == 0) {
		minNumOfRecsBetween = 1;
	}
	// Then we assume that the two rectangles shifted to each other as close as
	// possible, and their sizes are the greatest possible.
	int minDistanceLeft = Math.max(0, distanceBetweenPoints-(sizeRange.max-1)*2-borderWidth-1);
	// Max and min distances are sum of lengths of each rectangle in between two
	// rectangles on ends, plus borderWidth for each of those rectangles in
	// between.
	
	// If they are shifted so close that there can't be another of them, but at
	// maximum possible distance there can be at least one rectangle, shift them
	// away from each other so there can be at least one smallest possible
	// rectangle.
	if (minDistanceLeft < sizeRange.min && maxDistanceLeft > sizeRange.min) {
		minDistanceLeft = sizeRange.min;
	}
	ArrayList<Integer> xCoordinates = new ArrayList<Integer>();
	ArrayList<Integer> yCoordinates = new ArrayList<Integer>();
	ArrayList<Integer> widths = new ArrayList<Integer>();
	ArrayList<Integer> heights = new ArrayList<Integer>();

	Point leftPoint;
	Point rightPoint;
	boolean swapped = false;
	if (toSecondRectangle == Side.W || toSecondRectangle == Side.N) {
		leftPoint = newPoint;
		rightPoint = lastPoint;
		swapped = true;
	} else {
		leftPoint = lastPoint;
		rightPoint = newPoint;
	}
	int amountOfRectangles;
	if (maxDistanceLeft > sizeRange.min) {
		// If there is some place between two rectangles on ends of the line,
		// where at least one other rectangle can be placed, start preparing 
		// those middle rectangles.
		Range distanceRange = new Range(minDistanceLeft, minDistanceLeft);
//		Range distanceRange = new Range(maxDistanceLeft, maxDistanceLeft);
		Range sizeRangeWithBorder = new Range(sizeRange.min + borderWidth, sizeRange.max + borderWidth);
		int[] lengths = splitRandomLengthIntoRandomPieces(
			distanceRange,
			sizeRangeWithBorder
		);
		amountOfRectangles = lengths.length+2;
		// First get length on the main axis (width or height) of all rectangles.
		ArrayList<Integer> listOfLengths = xIsGreater ? widths : heights;
		ArrayList<Integer> listOfMainCoords = xIsGreater ? xCoordinates : yCoordinates;
		int sumOfLengths = 0;
		for (int length : lengths) {
			sumOfLengths += length;
		}
		// How many points are there to start "lengths" part of system.
		int lengthsStartVariants = (xIsGreater ? newPoint.x - lastPoint.x : newPoint.y - lastPoint.y) - borderWidth - sumOfLengths;
		// Range of actual such possible points (keeping in mind that rectangle 
		// on the left point has min and max length).
		int minLengthsStart = Math.max(1, lengthsStartVariants-sizeRange.max+1);
		int maxLengthsStart = Math.min(sizeRange.max, lengthsStartVariants);
//		int lengthsStart = Chance.rand(minLengthsStart, maxLengthsStart);
		int lengthsStart = minLengthsStart;
		int leftRecLength = Chance.rand(
			Math.min(sizeRange.max, Math.max(sizeRange.min, 1+borderWidth+lengthsStart)),
			sizeRange.max
		);
		int leftRecCoord = (xIsGreater ? lastPoint.x : lastPoint.y) - leftRecLength  + lengthsStart;
		listOfLengths.add(leftRecLength);
		for (int length : lengths) {
			listOfLengths.add(length-borderWidth);
		}
		int rightRecWidth = Chance.rand(
			Math.min(sizeRange.max, Math.max(sizeRange.min, lengthsStartVariants - lengthsStart + 1)),
			sizeRange.max
		);
		System.out.println(leftRecLength+" "+rightRecWidth);
		System.out.println(lengthsStartVariants+" "+lengthsStart);
		int rightRecCoord = (xIsGreater ? lastPoint.x : lastPoint.y) + sumOfLengths + borderWidth + lengthsStart;
		listOfLengths.add(rightRecWidth);
		// Then we get coordinates of rectangles on the main axis.
		// Here sum of lengths means another sum of lengths: it changes for each 
		// Rectangle from the leftmost to the rightmost.
		sumOfLengths = leftRecLength + borderWidth;
		listOfMainCoords.add(leftRecCoord);
		for (int length : lengths) {
			listOfMainCoords.add(leftRecCoord + sumOfLengths);
			sumOfLengths += length;
		}
		listOfMainCoords.add(rightRecCoord);
	} else {
		// Otherwise just close up the rectangles on line ends so there are no
		// other rectangles between these two.
		amountOfRectangles = 2;
		int coordLeft;
		int coordRight;
		if (xIsGreater) {
			coordLeft = leftPoint.x;
			coordRight = rightPoint.x;
		} else {
			coordLeft = leftPoint.y;
			coordRight = rightPoint.y;
		}
		int farthestPossibleRightmostPointOnLeftRec = Math.min(coordLeft + sizeRange.max - 1, coordRight - borderWidth);
		int closestPossibleRightmostPointOnLeftRec = Math.max(coordRight - sizeRange.max + 1 - borderWidth, coordLeft - borderWidth);
		int rightmostPointOnLeftRec = Chance.rand(closestPossibleRightmostPointOnLeftRec, farthestPossibleRightmostPointOnLeftRec);
		int widthOfLeftRec = Chance.rand(rightmostPointOnLeftRec - coordLeft + 1, sizeRange.max);
		int widthOfRightRec = Chance.rand(coordRight - (rightmostPointOnLeftRec + borderWidth + 1) - 1, sizeRange.max);
		int coord2 = rightmostPointOnLeftRec + borderWidth + 1;
		int coord1 = rightmostPointOnLeftRec - widthOfLeftRec + 1;
		if (swapped) {
			if (xIsGreater) {
				xCoordinates.add(coord2);
				xCoordinates.add(coord1);
				widths.add(widthOfRightRec);
				widths.add(widthOfLeftRec);
			} else {
				yCoordinates.add(coord2);
				yCoordinates.add(coord1);
				heights.add(widthOfRightRec);
				heights.add(widthOfLeftRec);
			}
		} else {
			if (xIsGreater) {
				xCoordinates.add(coord1);
				xCoordinates.add(coord2);
				widths.add(widthOfLeftRec);
				widths.add(widthOfRightRec);
			} else {
				yCoordinates.add(coord1);
				yCoordinates.add(coord2);
				heights.add(widthOfLeftRec);
				heights.add(widthOfRightRec);
			}
		}
		throw new NotImplementedException();
	}
	// Now we get randomized positions of rectangles on the short axis.
	Point topPoint;
	Point bottomPoint;
	swapped = false;
	if (toSecondRectangleSecodary == Side.W || toSecondRectangleSecodary == Side.N) {
		topPoint = newPoint;
		bottomPoint = lastPoint;
	} else {
		topPoint = lastPoint;
		bottomPoint = newPoint;
	}
	int heightTop = Chance.rand(sizeRange);
	int heightBottom = Chance.rand(sizeRange);
	int coordTopPoint;
	int coordBottomPoint;
	if (xIsGreater) {
		coordTopPoint = topPoint.y;
		coordBottomPoint = bottomPoint.y;
	} else {
		coordTopPoint = topPoint.x;
		coordBottomPoint = bottomPoint.x;
	}
	int topRecCoord1 = coordTopPoint - heightTop + 1;
	int topRecCoord2 = coordBottomPoint - heightBottom + 1;
	int dSecondaryAxis = (topRecCoord2 - topRecCoord1) / amountOfRectangles;
	if (xIsGreater) {
		heights.add(heightTop);
		yCoordinates.add(topRecCoord1);
	} else {
		widths.add(heightTop);
		xCoordinates.add(topRecCoord1);
	}

	for (int i = 1; i < amountOfRectangles - 1; i++) {
		// Setting coordinates and heights (widths if !xIsGreater) of all the
		// rectangles except for the rectangles on Points
		if (xIsGreater) {
			yCoordinates.add(topRecCoord1 + dSecondaryAxis * i);
			heights.add(Chance.rand(sizeRange));
		} else {
			xCoordinates.add(topRecCoord1 + dSecondaryAxis * i);
			widths.add(Chance.rand(sizeRange));
		}
	}
	if (xIsGreater) {
		heights.add(heightBottom);
		yCoordinates.add(topRecCoord2);
	} else {
		widths.add(heightBottom);
		xCoordinates.add(topRecCoord2);
	}

	// Finally, place all rectangles.
	for (int i=0, l=widths.size(); i<l; i++) {
		RectangleArea r = addRectangleArea(new RectangleArea(
			xCoordinates.get(i),
			yCoordinates.get(i),
			widths.get(i),
			heights.get(i)
		));
		if (i == 0 && pointsToRectangles.size() == 0) {
			pointsToRectangles.put(lastPoint, r);
		}
		if (i == l-1 && pointsToRectangles.size() == 0) {
			pointsToRectangles.put(lastPoint, r);
		}
		if (swapped && i == 0) {
			pointsToRectangles.put(newPoint, r);
		}
		if (!swapped && i == l-1) {
			pointsToRectangles.put(newPoint, r);
		}
	}
	return this;
}
public static int[] splitRandomLengthIntoRandomPieces(Range randomLength, Range randomPieceLength) {
	int maxNumberOfPieces = randomLength.max / randomPieceLength.min;
	int minNumberOfPieces = randomLength.min / Math.min(randomPieceLength.max, randomLength.min);
	int numberOfPieces = Chance.rand(minNumberOfPieces, maxNumberOfPieces);

	int[] piecesLengths = new int[numberOfPieces];
	for (int i = 0; i < numberOfPieces; i++) {
		piecesLengths[i] = randomPieceLength.min;
	}
	int singlePiecesLeft = randomLength.max - (numberOfPieces * randomPieceLength.min);

	Random random = new Random();
	int probabilityFullLength = Chance.rand(randomLength) - randomLength.min;
	for (int i = 0; i < singlePiecesLeft; i++) {
		double probabilityPiece = Math.abs(random.nextGaussian());
		int increasingIndex = (int) Math.floor(probabilityPiece / 2 * numberOfPieces);
		if (increasingIndex >= numberOfPieces) {
			increasingIndex = numberOfPieces - 1;
		}
		piecesLengths[increasingIndex] += 1;
		if (singlePiecesLeft - i < probabilityFullLength) {
			break;
		}
	}
	return piecesLengths;
}
public static void visualize() {

}
public Collection<Point> getPoints() {
	return pointsToRectangles.keySet();

}
}
