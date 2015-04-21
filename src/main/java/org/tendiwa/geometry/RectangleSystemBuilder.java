package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.*;

public class RectangleSystemBuilder {
	private final int borderWidth;
	protected List<NamedRectSet> rectses = new ArrayList<>();
	private Map<String, NamedRectSet> names = new LinkedHashMap<>();

	protected RectangleSystemBuilder(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public RectangleSystemBuilder place(RectSet what, Placement where) {
		return place(null, what, where);
	}

	public int borderWidth() {
		return borderWidth;
	}

	public RectangleSystemBuilder place(String name, RectSet what, Placement where) {
		NamedRectSet rects = new NamedRectSet(where.placeIn(what, this), Optional.ofNullable(name));
		rectses.add(rects);
		rects.name().ifPresent(n -> names.put(n, rects));
		return this;
	}

	public RectSet done() {
		return new BasicRectangleSequence(ImmutableList.copyOf(rectses));
	}

	public RectSet getByName(String name) {
		if (!names.containsKey(name)) {
			throw new NullPointerException("No rectangle with name " + name + " in a builder");
		}
		return names.get(name);
	}

	/**
	 * Returns a Placeable with the specified index. Note that this operation is slow on large lists (O(n), because
	 * LinkedList is used there).
	 *
	 * @param index
	 * 	Index of Placeable
	 * @return Placeable under the specified index.
	 */
	public RectSet getByIndex(int index) {
		return rectses.get(index);
	}

	public RectangleSystemBuilder call(String name) {
		names.put(name, rectses.get(rectses.size()-1));
		return this;
	}
}

