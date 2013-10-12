package tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class RectangleSystemBuilder {
protected final LinkedList<EnhancedRectangle> rectangles = new LinkedList<>();
final RectangleSystem rs;
protected EnhancedRectangle rememberedRectangle;
private LinkedList<EnhancedRectangle> boundingRecs = new LinkedList<>();
private EnhancedRectangle rememberedBoundingRec;
private Map<String, Placeable> names = new HashMap<>();

protected RectangleSystemBuilder(int borderWidth) {
	this.rs = new RectangleSystem(borderWidth);
}

public RectangleSystemBuilder place(Placeable what, Placement where) {
	return place(null, what, where);
}

public RectangleSystemBuilder place(String name, Placeable what, Placement where) {
	what.prebuild(this);
	EnhancedRectangle r = where.placeIn(what, this);
	boundingRecs.add(r);
	if (name != null) {
		names.put(name, r);
	}
	return this;
}

public EnhancedRectangle getRectangleByPointer(RectanglePointer pointer) {
	switch (pointer) {
		case FIRST_RECTANGLE:
			return rectangles.getFirst();
		case LAST_RECTANGLE:
			return rectangles.getLast();
		case REMEMBERED_RECTANGLE:
			return rememberedRectangle;
		case LAST_BOUNDING_REC:
			return boundingRecs.getLast();
		case REMEMBERED_BOUNDING_REC:
			return rememberedBoundingRec;
		default:
			throw new NotImplementedException();
	}
}

public RectangleSystemBuilder rememberRectangle() {
	rememberedRectangle = rectangles.getLast();
	return this;
}

public EnhancedRectangle placeRectangle(int x, int y, int width, int height) {
	EnhancedRectangle r = rs.addRectangle(new EnhancedRectangle(x, y, width, height));
	rectangles.add(r);
	return r;
}

public RectangleSystem done() {
	return rs;
}

public void placeRectangle(EnhancedRectangle what, Placement where) {
	what.prebuild(this);
	where.placeIn(what, this);
}

public RectangleSystemBuilder rememberBoundingRec() {
	rememberedBoundingRec = boundingRecs.getLast();
	return this;
}

public Placeable getByName(String name) {
	if (!names.containsKey(name)) {
		throw new NullPointerException("No rectangle with name " + name + " in a builder");
	}
	return names.get(name);
}

/**
 * Returns a Placeable with the specified index. Note that this operation is slow on large lists (O(n), because LinkedList
 * is used there).
 *
 * @param index Index of Placeable
 * @return Placeable under the specified index.
 */
public EnhancedRectangle getByIndex(int index) {
	return boundingRecs.get(index);
}

public ImmutableList<EnhancedRectangle> getRectangles() {
	return ImmutableList.<EnhancedRectangle>builder().addAll(rectangles).build();
}
public EnhancedRectangle getLastBoundingRec() {
	return boundingRecs.getLast();
}
}

