package org.tendiwa.core;

import org.tendiwa.core.meta.Chance;
import org.tendiwa.core.meta.Range;
import org.tendiwa.geometry.EnhancedRectangle;

import java.awt.*;
import java.util.*;

/**
 * A RectangleSystem that is defined by a set of points and that consists of
 * RectnalgeAreas forming lines between these points.
 * 
 * Each RectangleArea has varying width and height.
 * 
 * {@link FuckingTrailRectangleSystem} guarantees that it will contain RectangleAreas
 * that contain given points inside them, for each defined point. RectangleAreas
 * between points are meant to be rather chaotic and unpredictable.
 * 
 * @author suseika
 */
public class FuckingTrailRectangleSystem extends GrowingRectangleSystem {
private static final int MIN_SECONDARY_AXIS_COVERING_SIZE = 4;
protected Point lastPoint;
protected Range sizeRange;
final HashMap<Point, EnhancedRectangle> pointsToRectangles = new HashMap<Point, EnhancedRectangle>();
public static boolean STOP = false;

public FuckingTrailRectangleSystem(int borderWidth, Range sizeRange, Point start) {
	super(borderWidth);
	if (sizeRange.max < 2) {
		throw new IllegalArgumentException("Maximum of sizeRange must be at least 2; your range is "+sizeRange);
	}
	if (sizeRange.min < 1) {
		throw new IllegalArgumentException("Minumum of sizeRange must be at least 1; your range is "+sizeRange);
	}
	lastPoint = start;
	this.sizeRange = sizeRange;
}
public FuckingTrailRectangleSystem buildToPoint(Point newPoint) {
	// You better not start trying to comprehend this code yourself. If you
	// really need to know how it works, better contact me — I'll happily 
	// explain its difficult moments (if I'll be able to remember how it works
	// myself).
	int dx = newPoint.x - lastPoint.x;
	int dy = newPoint.y - lastPoint.y;
	// First we work with distance on the greatest axis.
	int distanceBetweenPoints;
	CardinalDirection toSecondRectangle; // Side of new rectangles appearing on the
							// greatest axis
	CardinalDirection toSecondRectangleSecondary; // DirectionToBERemoved of new rectangles shifting on
									// the least axis
	boolean xIsGreater = dx >= dy;
	if (xIsGreater) {
		distanceBetweenPoints = Math.abs(dx);
		toSecondRectangle = (CardinalDirection) Directions.shiftToDirection(dx, 0);
		toSecondRectangleSecondary = (CardinalDirection) Directions.shiftToDirection(0, dy);
	} else {
		distanceBetweenPoints = Math.abs(dy);
		toSecondRectangle = (CardinalDirection) Directions.shiftToDirection(0, dy);
		toSecondRectangleSecondary = (CardinalDirection) Directions.shiftToDirection(dx, 0);
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
	if (toSecondRectangle == Directions.W || toSecondRectangle == Directions.N) {
		leftPoint = newPoint;
		rightPoint = lastPoint;
		swapped = true;
	} else {
		leftPoint = lastPoint;
		rightPoint = newPoint;
	}
	int amountOfRectangles;
	if (maxDistanceLeft > sizeRange.min) {
		// If there is some placeIn between two rectangles on ends of the line,
		// where at least one other rectangle can be placed, start preparing 
		// those middle rectangles.
		Range distanceRange = new Range(minDistanceLeft, maxDistanceLeft);
		Range sizeRangeWithBorder = new Range(sizeRange.min + borderWidth, sizeRange.max + borderWidth);
		int[] lengths = splitRandomLengthIntoRandomPieces(
			distanceRange,
			sizeRangeWithBorder,
			xIsGreater, 
			newPoint
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
		int lengthsStart = Chance.rand(minLengthsStart, maxLengthsStart);
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
		throw new UnsupportedOperationException();
	}
	// Now we get randomized positions of rectangles on the short axis.
	Point topPoint;
	Point bottomPoint;
	swapped = false;
	if (toSecondRectangleSecondary == Directions.W || toSecondRectangleSecondary == Directions.N) {
		topPoint = newPoint;
		bottomPoint = lastPoint;
	} else {
		topPoint = lastPoint;
		bottomPoint = newPoint;
	}
	// Get coordinates of both points on secondary axis
	int coordTopPoint;
	int coordBottomPoint;
	if (xIsGreater) {
		coordTopPoint = topPoint.y;
		coordBottomPoint = bottomPoint.y;
	} else {
		coordTopPoint = topPoint.x;
		coordBottomPoint = bottomPoint.x;
	}
	int dSecondaryAxis = 1;
	// Each of lengths by secondary axis is partially covered by next length
	// (except for the last rectangle), and sum of uncovered areas equals 
	// full length of system by secondary axis.
	// For each rectangle except for last we store what part of it is not 
	// covered by a next rectangle.
	int secondaryAxisUncoveredAreas[] = new int[amountOfRectangles];
	Collection<Integer> increaseableIndexes = new HashSet<Integer>();
	int sumOfUncoveredAreasExceptForLastRec = 0;
	for (int i=0; i<amountOfRectangles-1; i++) {
		secondaryAxisUncoveredAreas[i] += dSecondaryAxis;
		sumOfUncoveredAreasExceptForLastRec += dSecondaryAxis;
		increaseableIndexes.add(i);
	}
	// The last rectangle doesn't have a next one, so its minimum uncovered area is
	// set directly.
	secondaryAxisUncoveredAreas[amountOfRectangles-1] = sizeRange.min;
	int sumOfUncoveredAreas = sumOfUncoveredAreasExceptForLastRec + sizeRange.min;
	increaseableIndexes.add(amountOfRectangles-1);

	int lengthOfTopRec = Chance.rand(Math.max(secondaryAxisUncoveredAreas[0]+1, sizeRange.min), sizeRange.max);
	int lengthOfBottomRec = Chance.rand(sizeRange);
	int minSumOfUncoveredAreas = coordBottomPoint-coordTopPoint+1;
	int maxSumOfUncoveredAreas = minSumOfUncoveredAreas;
	int sumLengthBySecondaryAxis = Chance.rand(minSumOfUncoveredAreas, maxSumOfUncoveredAreas);
//	int sumLengthBySecondaryAxis = maxSumOfUncoveredAreas;
	while (sumOfUncoveredAreas < sumLengthBySecondaryAxis) {
		// Increase sizes of uncovered areas until their sum reaches 
		Integer[] indexes = increaseableIndexes.toArray(new Integer[] {});
		int index = indexes[Chance.rand(0, indexes.length-1)];
		secondaryAxisUncoveredAreas[index]++;
		if (secondaryAxisUncoveredAreas[index] >= sizeRange.max-MIN_SECONDARY_AXIS_COVERING_SIZE) {
			increaseableIndexes.remove(new Integer(index));
		}
		sumOfUncoveredAreas++;
		sumOfUncoveredAreasExceptForLastRec++;
	}
	// Now we have all lengths of uncovered areas, so next we get lengths of our
	// future rectangles. 
	ArrayList<Integer> lengthsBySecondaryAxis = xIsGreater ? heights : widths;
	lengthsBySecondaryAxis.add(lengthOfTopRec);
	for (int i=1, l=secondaryAxisUncoveredAreas.length-1; i<l; i++) {
		int uncoveredLength = secondaryAxisUncoveredAreas[i];
		int fullLength = uncoveredLength + Chance.rand(
			Math.max(sizeRange.min-uncoveredLength, MIN_SECONDARY_AXIS_COVERING_SIZE), 
			sizeRange.max-uncoveredLength
		);
		lengthsBySecondaryAxis.add(fullLength);
	}
	lengthsBySecondaryAxis.add(lengthOfBottomRec);
	// And finally set coordinates of rectangles by secondary axis.
	Range rangeOfTopRec = new Range(0, lengthOfTopRec-1);
	Range rangeOfBottomRec = new Range(
		sumOfUncoveredAreasExceptForLastRec-(coordBottomPoint-coordTopPoint),
		sumOfUncoveredAreasExceptForLastRec-(coordBottomPoint-coordTopPoint)+lengthOfBottomRec-1
	);
	Range rangesIntersection = rangeOfTopRec.intersection(rangeOfBottomRec);
	int shift = rangeOfTopRec.max;
	if (rangesIntersection == null) {
		System.out.println(rangeOfTopRec+" "+rangeOfBottomRec+" "+rangesIntersection+" "+sumOfUncoveredAreasExceptForLastRec+" "+(coordBottomPoint-coordTopPoint));
		STOP = true;
	}
	int topCoordOfCurrentRec = coordTopPoint - shift;
	ArrayList<Integer> listOfSecondaryCoords = xIsGreater ? yCoordinates : xCoordinates;
	listOfSecondaryCoords.add(topCoordOfCurrentRec);
	for (int i=0; i<amountOfRectangles-1; i++) {
		topCoordOfCurrentRec += secondaryAxisUncoveredAreas[i];
		listOfSecondaryCoords.add(topCoordOfCurrentRec);
	}

	// Finally, placeIn all rectangles.
	for (int i=0, l=widths.size(); i<l; i++) {
		EnhancedRectangle r = addRectangle(new EnhancedRectangle(
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
/**
 * How many cells are there by secondary axis from lastPoint to newPoint
 * inclusive.
 */
private int getAmountOfCellsOnSecondaryAxis(boolean xIsGreater, Point newPoint) {
	return Math.abs(xIsGreater ? lastPoint.y-newPoint.y : lastPoint.x-newPoint.x)+1;
}
public int[] splitRandomLengthIntoRandomPieces(Range randomLength, Range randomPieceLength, boolean xIsGreater, Point newPoint) {
	int maxNumberOfPieces = randomLength.max / randomPieceLength.min;
	int minNumberOfPieces = randomLength.min / Math.min(randomPieceLength.max, randomLength.min);
	/*
	 * Finds out how many rectangles there should at least be between the points
	 * (including those rectangles on the points) for the algorithm to be able to
	 * completely fill the secondary axis with rectangles. Algorithm needs to know
	 * this number before the main axis is split into rectangles, because if it
	 * doesn't, then there is a chance that it will not be able to done a
	 * RectnalgeSystem of connected rectangles because even maximum secondary size
	 * of all rectangles will not be sufficient for all of them to touch sides.
	 */
	int amountOfCellsOnSecondaryAxis = getAmountOfCellsOnSecondaryAxis(xIsGreater, newPoint);
	// -1 here is because last rectangle doesn't intersect with anything after it.
	// Possible value of sizeRange.min == 1 is considered illegal in constructor,
	// so there won't be any division by zero.
	int minAmountOfRectangles = (amountOfCellsOnSecondaryAxis-1) / (sizeRange.max-1);
	if ((amountOfCellsOnSecondaryAxis-1) % (sizeRange.max-1) != 0) {
		minAmountOfRectangles += 1;
	}
	
	int numberOfPieces = Chance.rand(
		Math.max(minNumberOfPieces, minAmountOfRectangles), 
		maxNumberOfPieces
	);
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
}
