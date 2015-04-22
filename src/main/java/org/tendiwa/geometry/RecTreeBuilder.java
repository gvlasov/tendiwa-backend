package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.*;

public final class RecTreeBuilder {
	protected List<NamedRecTree> rectses = new ArrayList<>();
	private Map<String, NamedRecTree> names = new LinkedHashMap<>();

	public RecTreeBuilder() {
	}

	public RecTreeBuilder place(RecTree what, Placement where) {
		return place(null, what, where);
	}

	public RecTreeBuilder place(String name, RecTree what, Placement where) {
		NamedRecTree rects = new NamedRecTree(where.placeIn(what, this), Optional.ofNullable(name));
		rectses.add(rects);
		rects.name().ifPresent(n -> names.put(n, rects));
		return this;
	}

	public RecTree done() {
		return new BasicRecTree(ImmutableList.copyOf(rectses));
	}

	public RecTree getByName(String name) {
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
	public RecTree getByIndex(int index) {
		return rectses.get(index);
	}

	public RecTreeBuilder call(String name) {
		names.put(name, rectses.get(rectses.size()-1));
		return this;
	}
}

