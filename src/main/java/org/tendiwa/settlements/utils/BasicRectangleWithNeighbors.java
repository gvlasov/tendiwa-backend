package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Rectangle;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A larger main rectangle with >= 0 other rectangles near it.
 * <p>
 * This class represents an approximation of a polygon with
 * axis-aligned rectangles.
 *
 * @see org.tendiwa.settlements.utils.RectangularBuildingLots For a way to create instances of this class.
 */
public final class BasicRectangleWithNeighbors implements RectangleWithNeighbors {
	private final Rectangle rectangle;
	private final ImmutableList<Rectangle> neighbors;

	BasicRectangleWithNeighbors(List<Rectangle> rectangles) {
		this(
			rectangles.get(0),
			ImmutableList.copyOf(
				rectangles.subList(1, rectangles.size())
			)
		);
	}

	public BasicRectangleWithNeighbors(Rectangle rectangle, ImmutableList<Rectangle> neighbors) {
		Objects.requireNonNull(rectangle);
		Objects.requireNonNull(neighbors);
		this.rectangle = rectangle;
		this.neighbors = neighbors;
	}

	@Override
	public Rectangle mainRectangle() {
		return rectangle;
	}

	@Override
	public ImmutableList<Rectangle> neighbors() {
		return neighbors;
	}

	/**
	 * Allows iterating over the {@link #rectangle} and its {@link #neighbors} as if they were just a single list of
	 * {@link org.tendiwa.geometry.Rectangle}s.
	 */
	@Override
	public Iterable<Rectangle> allRectangles() {
		return new Iterable<Rectangle>() {
			@Override
			public Iterator<Rectangle> iterator() {
				return new Iterator<Rectangle>() {
					private Rectangle current = rectangle;
					private int index = 0;
					private final int size = neighbors.size();
					private boolean hasNext = true;

					@Override
					public boolean hasNext() {
						return hasNext;
					}

					@Override
					public Rectangle next() {
						if (index == size) {
							hasNext = false;
							return current;
						} else {
							Rectangle previous = current;
							current = neighbors.get(index);
							index++;
							return previous;
						}
					}
				};
			}

			@Override
			public void forEach(Consumer<? super Rectangle> action) {
				action.accept(rectangle);
				neighbors.forEach(action::accept);
			}
		};
	}

}
