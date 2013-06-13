package tendiwa.core;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tendiwa.core.meta.Coordinate;
import tendiwa.core.meta.Direction;
import tendiwa.core.meta.Side;
import tendiwa.core.meta.Utils;
import tendiwa.recsys.RandomRectangleSystem;
import tendiwa.recsys.RectangleArea;
import tendiwa.recsys.RectangleSystem;


/**
 * Wrapper above a RectangleSystem that modifies terrain using RectangleSyetms's
 * data.
 */
public class TerrainModifier {
private RectangleSystem rs;
private Location location;

private int wall = StaticData.getObjectType("wall_grey_stone").getId();

public TerrainModifier(Location location, int startX, int startY, int width, int height, int minRectangleWidth, int borderWidth) {
	rs = new RandomRectangleSystem(startX, startY, width, height, minRectangleWidth, borderWidth);
	this.location = location;
}

public TerrainModifier(Location location, RectangleSystem crs) {
	rs = crs;
	this.location = location;
}

public RectangleSystem getRectangleSystem() {
	return rs;
}

/**
 * Excludes rectangles that contain a particular entity inside them.
 * 
 * @param type
 * @param val
 */
public void excludeRectanglesHaving(int type, int val) {
	keyLoop: for (RectangleArea r : rs.getContent()) {
		for (int x = r.x; x < r.x + r.width; x++) {
			for (int y = r.y; y < r.y + r.height; y++) {
				if (location.cells[x][y].getElement(type) == val) {
					rs.excludeRectangle(r);
					continue keyLoop;
				}
			}
		}
	}
}
/**
 * Draws borders between neighbor rectangles. Note that sets of cells of which
 * inner and outer borders consist never intersect.
 * 
 * @see TerrainModifier#drawOuterBorders(int, int)
 * @param type
 * @param name
 * @author suseika
 */
public void drawInnerBorders(int type, int name) {
	/*
	 * For anyone who is going to read the code of this method: the code may be
	 * really hard to understand, because it is hard to describe with words
	 * rather than images what is going on here. If you need to know some
	 * particular details about this method, please feel free to contact me and
	 * ask.
	 */
	if (!rs.isBuilt()) {
		throw new RuntimeException("System must be built before calling this method");
	}
	if (rs.getBorderWidth() < 1) {
		throw new RuntimeException("Can't draw borders of RectangleSystem with borderWidth="+rs.getBorderWidth());
	}
	for (RectangleArea r1 : rs.rectangleSet()) {
		/*
		 * Look at each side of each rectangle if there is an inner border to
		 * draw.
		 */
		for (Side side : Side.EACH_CARDINAL_SIDE) {
			ArrayList<RectangleArea> rectanglesFromThatSide = new ArrayList<RectangleArea>(rs.getRectanglesCloseToSideOrBorder(r1, side));
			if (rectanglesFromThatSide.size() == 0) {
				continue;
			}
			// Sort rectangles so they go from top/left to bottom/right in
			// the ArrayList (it will be explained further why we need to
			// sort this array).
			if (side == Side.N || side == Side.S) {
				Collections.sort(rectanglesFromThatSide, RectangleSystem.horizontalRectangleComparator);
			} else {
				Collections.sort(rectanglesFromThatSide, RectangleSystem.verticalRectangleComparator);
			}
			/*
			 * Now we eliminate trailing rectangles — the rectangles which are
			 * close to r1, but don't have neighbors that are neighbors to r1.
			 * This may occur if the close rectangle only touches the border of
			 * r. Only the first and the last rectangles can be trailing.
			 */
			int size = rectanglesFromThatSide.size();
			/*
			 * These values indicate which rectangles are trailing. We need
			 * them, because we need the trailing rectangles to present in
			 * $rectanglesFromThatSide to check if a rectangle is trailing. This
			 * is needed for the case when there are only two close rectangles
			 * found, and both of them are trailing.
			 */
			boolean removeFirst = false;
			boolean removeLast = false;
			if (size == 1 && !rs.areRectanglesNear(r1, rectanglesFromThatSide.get(0))) {
				/*
				 * If the only close rectangle is not a neighbor to r1, then
				 * there is nothing to compute for this side.
				 */
				continue;
			}
			if (size > 1 && !rs.areRectanglesNear(rectanglesFromThatSide.get(0), r1) && !rs.areRectanglesNear(rectanglesFromThatSide.get(0), rectanglesFromThatSide.get(1))) {
				/*
				 * If the first close rectangle is not a neighbor to r1, and is
				 * not a neighbor to the second close rectangle, remove it.
				 */
				removeFirst = true;
			}
			if (size > 1 && !rs.areRectanglesNear(rectanglesFromThatSide.get(size - 1), r1) && !rs.areRectanglesNear(rectanglesFromThatSide.get(size - 2), rectanglesFromThatSide.get(size - 1))) {
				/*
				 * If the last close rectangle is not a neighbor to r1, and is
				 * not a neighbor to the previous close rectangle, remove it.
				 */
				removeLast = true;
			}
			if (removeFirst) {
				rectanglesFromThatSide.remove(0);
			}
			if (removeLast) {
				// Can't use variable size here because the size is changed if
				// removeFirst is true.
				rectanglesFromThatSide.remove(rectanglesFromThatSide.size() - 1);
			}
			if (rectanglesFromThatSide.size() == 0) {
				/*
				 * If there were only rectangles that don't have common segments
				 * with r, then there is nothing to compute.
				 */
				continue;
			}
			ArrayList<Segment> segments = new ArrayList<Segment>();
			// Knowing of close rectangles, create a list of segments. Each
			// neighbor rectangle corresponds to a segment.
			for (RectangleArea r2 : rectanglesFromThatSide) {
				Side side2 = side.opposite();
				// A segment on the border of close rectangle from side
				// opposite to the original r1's side.
				Segment segment;
				if (side2 == Side.N) {
					segment = new Segment(r2.x, r2.y, r2.width, Direction.H);
				} else if (side2 == Side.E) {
					segment = new Segment(r2.x + r2.width - 1, r2.y, r2.height, Direction.V);
				} else if (side2 == Side.S) {
					segment = new Segment(r2.x, r2.y + r2.height - 1, r2.width, Direction.H);
				} else {
					// if (side2 == SideTest.W)
					segment = new Segment(r2.x, r2.y, r2.height, Direction.V);
				}
				segments.add(segment);
			}
			/*
			 * Now combine the segments of those close rectangles that are
			 * neighbors to each other into one larger segment that starts where
			 * top/left rectangle's top/left side is and ends where bottom/right
			 * rectangle's bottom/right side is.
			 */
			for (int i = 0; i < segments.size() - 1; i++) {
				/*
				 * ^^^^ Size changes in loop; -1 is because the last rectangle
				 * shouldn't be checked.
				 */
				/*
				 * Rectangles in the array are sorted, and segments in their
				 * array have the same indexes and the rectangles corresponding
				 * to these segments, that's why we consecutively take two
				 * rectangles and combine their segments, if they should be
				 * combined.
				 */
				RectangleArea nextRectangle = rectanglesFromThatSide.get(i + 1);
				if (rs.areRectanglesNear(rectanglesFromThatSide.get(i), nextRectangle)) {
					segments.get(i).changeLength(segments.get(i + 1).length + rs.getBorderWidth());
					segments.remove(i + 1);
					i--;// So the next time the segment after the deleted
						// segment will be applied to the result segment.
				}
			}
			/*
			 * For the next step $1shortenSegments we will need to find
			 * neighbors of r1 from the next clockwise and the previous
			 * counter-clockwise sides whose side %side% is on the same line
			 * that r1's side. This is needed for one particular case when
			 * borders go in a shape of a cross: without this step there would
			 * be a hole right in the center of the cross because filling that
			 * cell depends on whether there are abovementioned neighbors or
			 * not.
			 */
			boolean hasPrevSameLineNeighbor = false; 	// Has this kind of
														// neighbor from N/W
														// side
			boolean hasNextSameLineNeighbor = false; 	// Has this kind of
														// neighbor from S/E
														// side
			if (side == Side.N) {
				Set<RectangleArea> neighborsW = rs.getNeighborsFromSide(r1, Side.W);
				Set<RectangleArea> neighborsE = rs.getNeighborsFromSide(r1, Side.E);
				for (RectangleArea r : neighborsW) {
					if (r.y == r1.y) {
						hasPrevSameLineNeighbor = true;
						break;
					}
				}
				for (RectangleArea r : neighborsE) {
					if (r.y == r1.y) {
						hasNextSameLineNeighbor = true;
						break;
					}
				}
			} else if (side == Side.E) {
				Set<RectangleArea> neighborsN = rs.getNeighborsFromSide(r1, Side.N);
				Set<RectangleArea> neighborsS = rs.getNeighborsFromSide(r1, Side.S);
				for (RectangleArea r : neighborsN) {
					if (r.x + r.width == r1.x + r1.width) {
						hasPrevSameLineNeighbor = true;
						break;
					}
				}
				for (RectangleArea r : neighborsS) {
					if (r.x + r.width == r1.x + r1.width) {
						hasNextSameLineNeighbor = true;
						break;
					}
				}
			} else if (side == Side.S) {
				Set<RectangleArea> neighborsW = rs.getNeighborsFromSide(r1, Side.W);
				Set<RectangleArea> neighborsE = rs.getNeighborsFromSide(r1, Side.E);
				for (RectangleArea r : neighborsW) {
					if (r.y + r.height == r1.y + r1.height) {
						hasPrevSameLineNeighbor = true;
						break;
					}
				}
				for (RectangleArea r : neighborsE) {
					if (r.y + r.height == r1.y + r1.height) {
						hasNextSameLineNeighbor = true;
						break;
					}
				}
			} else if (side == Side.W) {
				Set<RectangleArea> neighborsN = rs.getNeighborsFromSide(r1, Side.N);
				Set<RectangleArea> neighborsS = rs.getNeighborsFromSide(r1, Side.S);
				for (RectangleArea r : neighborsN) {
					if (r.x == r1.x) {
						hasPrevSameLineNeighbor = true;
						break;
					}
				}
				for (RectangleArea r : neighborsS) {
					if (r.x == r1.x) {
						hasNextSameLineNeighbor = true;
						break;
					}
				}
			}
			/*
			 * $1shortenSegments: The first and the last segment (even if there
			 * is only one segment) now can start or end at a cell that is not
			 * in front of r's side. If so, move start or end of such segment so
			 * that it starts/ends at the start/end of r1's side.
			 */
			if (side == Side.N || side == Side.S) {
				Segment segment = segments.get(0);
				if (segment.x < r1.x) {
					segment.length -= r1.x - (hasPrevSameLineNeighbor ? 1 : 0) - segment.x;
					segment.x = r1.x - (hasPrevSameLineNeighbor ? 1 : 0);
				}
				segment = segments.get(segments.size() - 1);
				if (segment.x + segment.length > r1.x + r1.width) {
					segment.length = segment.length - (segment.x + segment.length - (r1.x + r1.width)) + (hasNextSameLineNeighbor ? 1 : 0);
				}
			} else if (side == Side.E || side == Side.W) {
				Segment segment = segments.get(0);
				if (segment.y < r1.y) {
					segment.length -= r1.y - (hasPrevSameLineNeighbor ? 1 : 0) - segment.y;
					segment.y = r1.y - (hasPrevSameLineNeighbor ? 1 : 0);
				}
				segment = segments.get(segments.size() - 1);
				if (segment.y + segment.length > r1.y + r1.height) {
					segment.length = segment.length - (segment.y + segment.length - (r1.y + r1.height)) + (hasNextSameLineNeighbor ? 1 : 0);
				}
			}
			/*
			 * The segments are now right on borders of close rectangles, so
			 * before drawing segments have to be moved so they are on inner
			 * borders. The segments are also drawn here.
			 */
			if (side == Side.N) {
				for (Segment s : segments) {
					s.changeY(rs.getBorderWidth());
					location.drawSegment(s, rs.getBorderWidth(), type, name);
				}
			} else if (side == Side.E) {
				for (Segment s : segments) {
					s.changeX(-rs.getBorderWidth());
					location.drawSegment(s, rs.getBorderWidth(), type, name);
				}
			} else if (side == Side.S) {
				for (Segment s : segments) {
					s.changeY(-rs.getBorderWidth());
					location.drawSegment(s, rs.getBorderWidth(), type, name);
				}
			} else {
				// if (side == SideTest.W)
				for (Segment s : segments) {
					s.changeX(rs.getBorderWidth());
					location.drawSegment(s, rs.getBorderWidth(), type, name);
				}
			}
		}
	}
}
/**
 * Draws borders around rectangles from those their segments that don't touch
 * any neighbor rectangle. Note that sets of cells of which inner and outer
 * borders consist never intersect.
 * 
 * @see TerrainModifier#drawInnerBorders(int, int)
 * @param type
 * @param name
 */
public void drawOuterBorders(int type, int name) {
	if (!rs.isBuilt()) {
		throw new RuntimeException("System must be built before calling this method");
	}
	if (rs.getBorderWidth() < 1) {
		throw new RuntimeException("Can't draw borders of RectangleSystem with borderWidth="+rs.getBorderWidth());
	}
	for (RectangleArea r : rs.getOuterSides().keySet()) {
		Set<Side> sides = rs.getOuterSides().get(r);
		for (Side side : sides) {
			Set<Segment> segments = rs.getSegmentsFreeFromNeighbors(r, side);
			for (Segment segment : segments) {
				Segment borderSegment = segment.clone();
				if (side == Side.N) {
					borderSegment.changeY(-rs.getBorderWidth());
					borderSegment.changeX(-rs.getBorderWidth());
				} else if (side == Side.E) {
					borderSegment.changeX(rs.getBorderWidth());
					borderSegment.changeY(-rs.getBorderWidth());
				} else if (side == Side.S) {
					borderSegment.changeY(rs.getBorderWidth());
					borderSegment.changeX(-rs.getBorderWidth());
				} else if (side == Side.W) {
					borderSegment.changeX(-rs.getBorderWidth());
					borderSegment.changeY(-rs.getBorderWidth());
				}
				borderSegment.changeLength(rs.getBorderWidth() * 2);
				location.drawSegment(borderSegment, rs.getBorderWidth(), Location.ELEMENT_OBJECT, wall);
			}
		}
	}
}

public void connectCornersWithLines(int type, int value, int padding, boolean considerBorderWidth) {
	Rectangle boundingRec = rs.getBoundingRectangle();
	ccwlLastCellHolder.center = new Coordinate(Math.round(boundingRec.x + boundingRec.width / 2), Math.round(boundingRec.y + boundingRec.height / 2));
	ArrayList<Coordinate> corners = new ArrayList<Coordinate>();
	Comparator<Coordinate> comparator = new Comparator<Coordinate>() {
		@Override
		public int compare(Coordinate c1, Coordinate c2) {
			return new Double(Utils.getLineAngle(ccwlLastCellHolder.center, c1)).compareTo(Utils.getLineAngle(ccwlLastCellHolder.center, c2));
		}
	};
	for (RectangleArea r : rs.rectangleSet()) {
		Set<Side> sides = rs.getOuterSides().get(r);
		boolean n = sides.contains(Side.N);
		boolean e = sides.contains(Side.E);
		boolean s = sides.contains(Side.S);
		boolean w = sides.contains(Side.W);
		if (n && e) {
			corners.add(new Coordinate(r.x + r.width - 1 + (considerBorderWidth ? rs.getBorderWidth() : 0) + padding, r.y + (considerBorderWidth ? -rs.getBorderWidth() : 0) - padding));

		}
		if (e && s) {
			corners.add(new Coordinate(r.x + r.width - 1 + (considerBorderWidth ? rs.getBorderWidth() : 0) + padding, r.y + r.height - 1 + (considerBorderWidth ? rs.getBorderWidth() : 0) + padding));
		}
		if (s && w) {
			corners.add(new Coordinate(r.x + (considerBorderWidth ? -rs.getBorderWidth() : 0) - padding, r.y + r.height - 1 + (considerBorderWidth ? rs.getBorderWidth() : 0) + padding));
		}
		if (w && n) {
			corners.add(new Coordinate(r.x + (considerBorderWidth ? -rs.getBorderWidth() : 0) - padding, r.y + (considerBorderWidth ? -rs.getBorderWidth() : 0) - padding));
		}
	}
	Collections.sort(corners, comparator);
	Coordinate c1 = corners.get(0);
	int size = corners.size();
	for (int i = 1; i < size; i++) {
		Coordinate c2 = corners.get(i);
		location.line(c1.x, c1.y, c2.x, c2.y, type, value);
		c1 = c2;
		if (i == size - 1) {
			location.line(c2.x, c2.y, corners.get(0).x, corners.get(0).y, type, value);
		}

	}
}

public void fillContents(int type, int value) {
	for (Rectangle r : rs.rectangleSet()) {
		location.square(r, type, value, true);
	}
}

private static class ccwlLastCellHolder {
// A class that is used by connectCornersWithLines() method to store
// lastCell variable for custom comparator.
public static Coordinate center;
}

public void drawLines() {
	Graph<RectangleArea, DefaultEdge> graph = rs.getGraph();
	for (DefaultEdge e : graph.edgeSet()) {
		Coordinate c1 = graph.getEdgeSource(e).getCenterPoint();
		Coordinate c2 = graph.getEdgeTarget(e).getCenterPoint();
		location.line(c1.x, c1.y, c2.x, c2.y, Location.ELEMENT_OBJECT, wall);
	}
}
}
