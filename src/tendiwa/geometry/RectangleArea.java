package tendiwa.geometry;

import java.awt.Rectangle;

import tendiwa.core.UniqueObject;

/**
 * Used in {@link RectangleSystem}. A unique id is automatically assigned to
 * each RectangleArea — this is its only difference from
 * {@link EnhancedRectangle}m and it allows RectangleAreas to be used in
 * RectangleSystems.
 */
public class RectangleArea extends EnhancedRectangle {
	private static final long serialVersionUID = 5212347063412511714L;
	/**
	 * Id in RectangleSystem. Each RectangleArea in a RectangleSystem has a
	 * unique id.
	 */
	private UniqueObject uniqueness = new UniqueObject();
	/**
	 * 
	 * @param r
	 *            A {@link Rectangle} representing this RectangleArea
	 * @param id
	 *            Id in RectangleSystem. Each RectangleArea in a RectangleSystem
	 *            has a unique id.
	 */
	RectangleArea(Rectangle r) {
		super(r);
	}
	public RectangleArea(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	public String toString() {
		return "{"+uniqueness.id+","+x+","+y+","+width+","+height+"}";
	}
	public int getId() {
		return uniqueness.id;
	}
	public int hashCode() {
		return uniqueness.id;
	}
}
