package tendiwa.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tendiwa.core.meta.Chance;

public class InterrectangularPath implements Iterable<RectangleArea> {
	final HashMap<Integer, RectangleArea> locationPlaces = new HashMap<Integer, RectangleArea>();
	final HashMap<RectangleArea, HashSet<RectanglesJunction>> junctions = new HashMap<RectangleArea, HashSet<RectanglesJunction>>();
	protected final int width;
	protected final RectangleSystem rs;
	public InterrectangularPath(RectangleSystem rs, int width) {
		this.rs = rs;
		this.width = width;
	}
	public InterrectangularPath addNextRectangle(RectangleArea r) {
		locationPlaces.put(locationPlaces.size(), r);
		junctions.put(r, new HashSet<RectanglesJunction>());
		if (junctions.size() > 0) {
			putJunctionRangomlyInSegment(r, getRectangleAt(locationPlaces.size()-1));
		}
		return this;
	}
	private void putJunctionRangomlyInSegment(RectangleArea r1, RectangleArea r2) {
		Segment segment = rs.getAdjacencySegment(r1, r2);
		RectanglesJunction junction = new RectanglesJunction(
			segment.getOrientation(), 
			Chance.rand(segment.getStartCoord(), segment.getEndCoord()),
			width,
			r1,
			r2
		);
		// Associate the newly created junction with both rectangles it connects.
		junctions.get(r1).add(junction);
		junctions.get(r2).add(junction);
	}
	public boolean junctionCanBePlacedBetween(RectangleArea r1, RectangleArea r2) {
		return rs.getAdjacencySegment(r1, r2).getLength() >= width;
	}
	private RectangleArea getRectangleAt(int index) {
		return locationPlaces.get(index);
	}
	/**
	 * Returns a collection of RectnagleAreas that are joinable with the last
	 * RectangleArea in this InterrectangularPath (i.e. last RectangleArea and its
	 * neighbor have at least %width% connected cells.)
	 * @return
	 */
	public Collection<RectangleArea> getRectanglesJoinableWithLast() {
		RectangleArea lastRectangle = getLastRectangle();
		Set<RectangleArea> rectangles = rs.getNeighbors(lastRectangle);
		Collection<RectangleArea> answer = new HashSet<RectangleArea>();
		for (RectangleArea r : rectangles) {
			if (junctionCanBePlacedBetween(r, lastRectangle)) {
				answer.add(r);
			}
		}
		return answer;
	}
	public RectangleArea getLastRectangle() {
		return locationPlaces.get(locationPlaces.size());
	}
	@Override
	public Iterator<RectangleArea> iterator() {
		return new Iterator<RectangleArea>() {
			int index = -1;
			@Override
			public boolean hasNext() {
				return index == locationPlaces.size()-1;
			}

			@Override
			public RectangleArea next() {
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
	 * Returns all junctions between rectangles present in this InterrectangularPath
	 * @return
	 */
	public List<RectanglesJunction> getJunctions() {
		ArrayList<RectanglesJunction> list = new ArrayList<RectanglesJunction>();
		for (HashSet<RectanglesJunction> junctionsOfRectangle : junctions.values()) {
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
	 * @param r1
	 * @param r2
	 * @return
	 */
	public RectanglesJunction getJunction(RectangleArea r1, RectangleArea r2) {
		if (r1.equals(r2)) {
			throw new Error("Trying to get a junction between two equal RectangleAreas");
		}
		for (RectanglesJunction rj : junctions.get(r1)) {
			if(rj.r1 == r2 || rj.r2 == r2) {
				return rj;
			}
		}
		return null;
	}
}
