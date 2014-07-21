package org.tendiwa.settlements;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Rectangle;

public class RectangleWithNeighbors {
	public final Rectangle rectangle;
	public final ImmutableList<Rectangle> neighbors;

	public RectangleWithNeighbors(Rectangle rectangle, ImmutableList<Rectangle> neighbors) {
		this.rectangle = rectangle;
		this.neighbors = neighbors;
	}
}
