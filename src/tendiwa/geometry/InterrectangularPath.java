package tendiwa.geometry;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tendiwa.core.meta.Chance;

import java.util.*;

public class InterrectangularPath implements Iterable<EnhancedRectangle> {
	final HashMap<Integer, EnhancedRectangle> locationPlaces = new HashMap<Integer, EnhancedRectangle>();
	final HashMap<EnhancedRectangle, HashSet<RectanglesJunction>> junctions = new HashMap<EnhancedRectangle, HashSet<RectanglesJunction>>();
	protected final int width;
	protected final RectangleSystem rs;

	public InterrectangularPath(RectangleSystem rs, int width) {
		this.rs = rs;
		this.width = width;
	}
	public InterrectangularPath addNextRectangle(EnhancedRectangle r) {
		locationPlaces.put(locationPlaces.size(), r);
		junctions.put(r, new HashSet<RectanglesJunction>());
		if (junctions.size() > 0) {
			putJunctionRangomlyInSegment(
				r,
				getRectangleAt(locationPlaces.size() - 1));
		}
		return this;
	}
	private void putJunctionRangomlyInSegment(EnhancedRectangle r1, EnhancedRectangle r2) {
		Segment segment = rs.getAdjacencySegment(r1, r2);
		RectanglesJunction junction = new RectanglesJunction(
			segment.getOrientation(),
			Chance.rand(segment.getStartCoord(), segment.getEndCoord()),
			width,
			r1,
			r2);
		// Associate the newly created junction with both rectangles it
		// connects.
		junctions.get(r1).add(junction);
		junctions.get(r2).add(junction);
	}
	public boolean junctionCanBePlacedBetween(EnhancedRectangle r1, EnhancedRectangle r2) {
		return rs.getAdjacencySegment(r1, r2).getLength() >= width;
	}
	private EnhancedRectangle getRectangleAt(int index) {
		return locationPlaces.get(index);
	}
	/**
	 * Returns a collection of RectnagleAreas that are joinable with the last
	 * EnhancedRectangle in this InterrectangularPath (i.e. last EnhancedRectangle and
	 * its neighbor have at least {@code width} connected cells.)
	 * 
	 * @return
	 */
	public Collection<EnhancedRectangle> getRectanglesJoinableWithLast() {
		EnhancedRectangle lastRectangle = getLastRectangle();
		Set<EnhancedRectangle> rectangles = rs.getNeighbors(lastRectangle);
		Collection<EnhancedRectangle> answer = new HashSet<EnhancedRectangle>();
		for (EnhancedRectangle r : rectangles) {
			if (junctionCanBePlacedBetween(r, lastRectangle)) {
				answer.add(r);
			}
		}
		return answer;
	}
	public EnhancedRectangle getLastRectangle() {
		return locationPlaces.get(locationPlaces.size());
	}
	@Override
	public Iterator<EnhancedRectangle> iterator() {
		return new Iterator<EnhancedRectangle>() {
			int index = -1;

			@Override
			public boolean hasNext() {
				return index == locationPlaces.size() - 1;
			}

			@Override
			public EnhancedRectangle next() {
				index++;
				return locationPlaces.get(index);
			}

			@Override
			public void remove() {
				throw new NotImplementedException();
			}

		};
	}
	/**
	 * Returns all junctions between rectangles present in this
	 * InterrectangularPath
	 * 
	 * @return
	 */
	public List<RectanglesJunction> getJunctions() {
		ArrayList<RectanglesJunction> list = new ArrayList<RectanglesJunction>();
		for (HashSet<RectanglesJunction> junctionsOfRectangle : junctions
			.values()) {
			for (RectanglesJunction junction : junctionsOfRectangle) {
				if (!list.contains(junction)) {
					list.add(junction);
				}
			}
		}
		return list;
	}
	/**
	 * Returns a junction between two particular rectangles.
	 * 
	 * @param r1
	 *            a rectangle present in this {@link RectangleSystem}.
	 * @param r2
	 *            a neighbor of that rectangle, also present in this
	 *            {@link RectangleSystem}.
	 * @return the junction between those rectangles, or {@code null} if two
	 *         rectangles are not neighbors.
	 * @throws IllegalArgumentException
	 *             if r1 and r2 are not neighbors, or if {@code r1 == r2}
	 * @see RectangleSystem#areRectanglesNear(java.awt.Rectangle,
	 *      java.awt.Rectangle) for definition of what neighbor rectangles are.
	 */
	public RectanglesJunction getJunction(EnhancedRectangle r1, EnhancedRectangle r2) {
		if (r1.equals(r2)) {
			throw new IllegalArgumentException(
				"Trying to get a junction between two equal EnhancedRectangles");
		}
		if (!rs.areRectanglesNear(r1, r2)) {
			throw new IllegalArgumentException("Rectangles are not neighbors");
		}
		for (RectanglesJunction rj : junctions.get(r1)) {
			if (rj.r1 == r2 || rj.r2 == r2) {
				return rj;
			}
		}
		return null;
	}
}
