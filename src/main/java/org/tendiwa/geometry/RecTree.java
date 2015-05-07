package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;
import org.tendiwa.core.Orientation;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.tendiwa.collections.Collectors.toImmutableList;

/**
 * Shape that consists of rectangles.
 */
public interface RecTree {
	ImmutableCollection<NamedRecTree> parts();

	/**
	 * Returns a minimum rectangle that contains all rectangles in this template.
	 *
	 * @return Minimum rectangle that contains all rectangles in this template.
	 */
	Rectangle bounds();

	RecTree part(String name);

	RecTree nestedPart(String name);


	default RecTree translate(int dx, int dy) {
		return new ModifiedRecTree(this, p -> p.translate(dx, dy));
	}

	default RecTree moveTo(int x, int y) {
		Rectangle bounds = bounds();
		return new ModifiedRecTree(this, p -> p.translate(x - bounds.x(), y - bounds.y()));
	}

	default RecTree rotate(Rotation rotation) {
		return new ModifiedRecTree(this, p -> rotate(rotation));
	}

	default RecTree flip(Orientation orientation) {
		return new ModifiedRecTree(this, p -> p.flip(orientation));
	}


	/**
	 * Starts a chain of methods that create a new Placeable by repeating this one.
	 *
	 * @param count
	 * 	How many times to repeat this placeable.
	 * @return Next step of chain
	 */
	default StepPlaceNextAt repeat(int count) {
		return new StepPlaceNextAt(count, this);
	}

	default Stream<Rectangle> rectangles() {
		return parts().stream()
			.flatMap(RecTree::rectangles);
	}

	default boolean encloses(Rectangle rectangle) {
		Objects.requireNonNull(this);
		Objects.requireNonNull(rectangle);
		double recArea = rectangle.area();
		int intersectionArea = 0;

		for (Rectangle areaPiece : this.rectangles().collect(toImmutableList())) {
			Optional<Rectangle> intersection = areaPiece.intersection(rectangle);
			if (intersection.isPresent()) {
				intersectionArea += intersection.get().area();
			}
			if (intersectionArea == recArea) {
				return true;
			}
		}
		return false;
	}
}
