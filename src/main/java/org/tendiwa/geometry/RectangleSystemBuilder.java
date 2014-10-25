package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.*;

public class RectangleSystemBuilder {
	protected final LinkedList<Rectangle> rectangles = new LinkedList<>();
	protected final RectangleSystem rs;
	protected Rectangle rememberedRectangle;
	protected LinkedList<Placeable> placeables = new LinkedList<>();
	private Rectangle rememberedBoundingRec;
	private Map<String, Placeable> names = new HashMap<>();
	protected RectangleSequence foundRectangles;

	protected RectangleSystemBuilder(int borderWidth) {
		this.rs = new RectangleSystem(borderWidth);
	}

	public RectangleSystemBuilder place(Placeable what, Placement where) {
		return place(null, what, where);
	}

	public RectangleSystemBuilder place(String name, Placeable what, Placement where) {
		what.prebuild(this);
		Rectangle r = where.placeIn(what, this);
		placeables.add(what);
		if (name != null) {
			names.put(name, r);
		}
		return this;
	}

	public Placeable getRectangleByPointer(RectanglePointer pointer) {
		switch (pointer) {
			case FIRST_RECTANGLE:
				return rectangles.getFirst();
			case LAST_RECTANGLE:
				return rectangles.getLast();
			case REMEMBERED_RECTANGLE:
				return rememberedRectangle;
			case LAST_BOUNDING_REC:
				return placeables.getLast().getBounds();
			case REMEMBERED_BOUNDING_REC:
				return rememberedBoundingRec;
			case FOUND_RECTANGLES:
				return foundRectangles;
			default:
				throw new IllegalArgumentException();
		}
	}

	public RectangleSystemBuilder rememberRectangle() {
		rememberedRectangle = rectangles.getLast();
		return this;
	}

	public Rectangle placeRectangle(int x, int y, int width, int height) {
		Rectangle r = rs.addRectangle(new Rectangle(x, y, width, height));
		rectangles.add(r);
		return r;
	}

	public RectangleSystem done() {
		return rs;
	}

	public void placeRectangle(Rectangle what, Placement where) {
		what.prebuild(this);
		where.placeIn(what, this);
	}

	public RectangleSystemBuilder rememberBoundingRec() {
		rememberedBoundingRec = placeables.getLast().getBounds();
		return this;
	}

	public Placeable getByName(String name) {
		if (!names.containsKey(name)) {
			throw new NullPointerException("No rectangle with name " + name + " in a builder");
		}
		return names.get(name);
	}

	/**
	 * Returns a Placeable with the specified index. Note that this operation is slow on large lists (O(n), because
	 * LinkedList
	 * is used there).
	 *
	 * @param index
	 * 	Index of Placeable
	 * @return Placeable under the specified index.
	 */
	public Placeable getByIndex(int index) {
		return placeables.get(index);
	}

	public ImmutableList<Rectangle> getRectangles() {
		return ImmutableList.<Rectangle>builder().addAll(rectangles).build();
	}

	public Rectangle getLastBoundingRec() {
		return placeables.getLast().getBounds();
	}

	protected Collection<Placeable> getPlaceables() {
		return Collections.unmodifiableList(placeables);
	}

}

