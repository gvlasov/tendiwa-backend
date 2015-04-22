package org.tendiwa.geometry;

public interface RectanglePointer {
	/**
	 * The first rectangle placed.
	 */
	static RectanglePointer first =  builder -> builder.rectses.get(0);
	/**
	 * The most recently placed rectangle.
	 */
	static RectanglePointer previous =  builder -> builder.rectses.get(builder
		.rectses.size() - 1);

	static RectanglePointer named(String name) {
		return builder -> builder.getByName(name);
	}

	RecTree find(RecTreeBuilder builder);
}
