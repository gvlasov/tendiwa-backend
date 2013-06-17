package tendiwa.geometry;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
	int dx = newPoint.x - lastPoint.x;
	int dy = newPoint.y - lastPoint.y;
	// First we work with distance on the greatest axis.
	int distanceLeft;
	Side toSecondRectangle;
	Side secondaryAxisIncreasingSide;
	boolean xIsGreater = dx >= dy;
	if (xIsGreater) {
		distanceLeft = Math.abs(dx);
		toSecondRectangle = Side.d2side(dx, 0);
		secondaryAxisIncreasingSide = Side.d2side(0, dy);
	} else {
		distanceLeft = dy;
		toSecondRectangle = Side.d2side(0, dy);
		secondaryAxisIncreasingSide = Side.d2side(dx, 0);
	}
	// First we assume that rectangles on the points are shifted away from each
	// other in such a way that distance between them is the greatest possible, and
	// the points are inside the rectangles, so only borders decrease the distance left.
	int maxDistanceLeft = distanceLeft - borderWidth*2;
	int maxNumOfRecsBetween = maxDistanceLeft / sizeRange.min;
	int minNumOfRecsBetween = maxDistanceLeft / sizeRange.max;
	if (maxNumOfRecsBetween > 1 && minNumOfRecsBetween == 0) {
		minNumOfRecsBetween = 1;
	}
	// Then we assume that the two rectangles shifted to each other as close as
	// possible, and their sizes are the greatest possible.
	int minDistanceLeft = Math.max(0, distanceLeft - (sizeRange.max-1)*2 - borderWidth*2);
	// If they shifted so close that there can't be another rectangle between them,
	// but at maximum possible distance there can be at least one rectangle, shift
	// them away from each other so there can be at least one smallest possible
	// rectangle.
	if (minDistanceLeft < sizeRange.min && maxDistanceLeft > sizeRange.min){
		minDistanceLeft = sizeRange.min;
	}
	ArrayList<Integer> xCoordinates = new ArrayList<Integer>();
	ArrayList<Integer> yCordinates = new ArrayList<Integer>();
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
	// If there is some place between two rectangles on ends of the line, where at
	// least one other rectangle can be placed, start preparing those middle
	// rectangles.
		amountOfRectangles = 0;
	} else {
	// Otherwise just close up the rectangles on line ends so there are no other
	// rectangles between these two.
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
		int farthestPossibleRightmostPointOnLeftRec = Math.min(
			coordLeft + sizeRange.max - 1,
			coordRight - borderWidth
			);
		int closestPossibleRightmostPointOnLeftRec = Math.max(
			coordRight - sizeRange.max + 1 - borderWidth,
			coordLeft - borderWidth
			);
		int rightmostPointOnLeftRec = Chance.rand(
			closestPossibleRightmostPointOnLeftRec, 
			farthestPossibleRightmostPointOnLeftRec
			);
		int widthOfLeftRec = Chance.rand(
			rightmostPointOnLeftRec - coordLeft + 1,
			sizeRange.max
			);
		int widthOfRightRec = Chance.rand(
			coordRight - (rightmostPointOnLeftRec + borderWidth + 1) - 1,
			sizeRange.max
			);
		int coord2 = rightmostPointOnLeftRec + borderWidth + 1;
		int coord1 = rightmostPointOnLeftRec - widthOfLeftRec + 1;
		if (swapped) {
			if (xIsGreater) {
				xCoordinates.add(coord2);
				xCoordinates.add(coord1);
				widths.add(widthOfRightRec);
				widths.add(widthOfLeftRec);
			} else {
				
				yCordinates.add(coord2);
				yCordinates.add(coord1);
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
				yCordinates.add(coord1);
				yCordinates.add(coord2);
				heights.add(widthOfLeftRec);
				heights.add(widthOfRightRec);
			}
		}
	}
	// Now we get randomized positions of rectangles on the short axis.
	Point topPoint;
	Point bottomPoint;
	swapped = false;
	if (secondaryAxisIncreasingSide == Side.W || secondaryAxisIncreasingSide == Side.N) {
		topPoint = newPoint;
		bottomPoint = lastPoint;
	} else {
		topPoint = lastPoint;
		bottomPoint = newPoint;
	}
	int heightTop = Chance.rand(sizeRange);
	int heightBottom = Chance.rand(sizeRange);
	int coordTop;
	int coordBottom;
	if (xIsGreater) {
		coordTop = topPoint.y;
		coordBottom = bottomPoint.y;
	} else {
		coordTop = topPoint.x;
		coordBottom = bottomPoint.x;
	}
	int topRecCoord1 = coordTop - heightTop + 1;
	int topRecCoord2 = coordBottom - heightBottom + 1;
	int dSecondaryAxis = (topRecCoord2 - topRecCoord1) / amountOfRectangles;
	for (int i=1; i<amountOfRectangles - 1; i++) {
		// Setting coordinates and heights (widths if !xIsGreater) of all the
		// rectangles except for the first and the last.
		if (xIsGreater) {
			yCordinates.add(topRecCoord1 + dSecondaryAxis*i);
			heights.add(Chance.rand(sizeRange));
		} else {
			xCoordinates.add(topRecCoord1 + dSecondaryAxis*i);
			widths.add(Chance.rand(sizeRange));
		}
	}
	
	// Finally, place all rectangles.
	return this;
}
public static int[] splitRandomLengthIntoRandomPieces(Range randomLength, Range randomPieceLength) {
	int dRandomLength = randomLength.max - randomLength.min;
	int maxNumberOfPieces = randomLength.max / randomPieceLength.min;
	int minNumberOfPieces = randomLength.min / Math.min(randomPieceLength.max, randomLength.min);
	int numberOfPieces = Chance.rand(minNumberOfPieces, maxNumberOfPieces);
	
	int[] piecesLengths = new int[numberOfPieces];
	for (int i=0; i<numberOfPieces; i++) {
		piecesLengths[i] = randomPieceLength.min;
	}
	int singlePiecesLeft = randomLength.max - (numberOfPieces * randomPieceLength.min);
	
	Random random = new Random();
	int probabilityFullLength = Chance.rand(randomLength) - randomLength.min;
	for (int i=0; i<singlePiecesLeft; i++) {
		double probabilityPiece = Math.abs(random.nextGaussian());
		int increasingIndex = (int) Math.floor(probabilityPiece/2*numberOfPieces);
		if (increasingIndex >= numberOfPieces) {
			increasingIndex = numberOfPieces-1;
		}
		piecesLengths[increasingIndex] += 1;
		if (singlePiecesLeft-i < probabilityFullLength) {
			break;
		}
	}
	int sum = 0;
	for (int i=0; i<numberOfPieces; i++) {
		sum += piecesLengths[i];
	}
	System.out.println(sum);
	return piecesLengths;
}
}
