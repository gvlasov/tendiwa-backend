package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a most basic placeable collection of non-overlapping rectangles. Unlike {@link RectangleSystem},
 * RectangleSequence doesn't maintain neighborship and outerness of rectangles.
 */
final class BasicRecTree implements RecTree {
	/**
	 * RectangleAreas that are parts of this RectangleSystem.
	 */
	private final ImmutableCollection<NamedRecTree> content;
	private final Map<String, NamedRecTree> named;

	/**
	 * Creates an empty RectangleSequence.
	 */
	BasicRecTree(ImmutableCollection<NamedRecTree> content) {
		this.content = content;
		this.named = createNameMap(content);
	}

	private Map<String, NamedRecTree> createNameMap(ImmutableCollection<NamedRecTree> content) {
		Map<String, NamedRecTree> map = new LinkedHashMap<>(content.size());
		content.forEach(
			r -> r.name().ifPresent(
				name -> map.put(name, r)
			)
		);
		return map;
	}

	@Override
	public ImmutableCollection<NamedRecTree> parts() {
		return content;
	}

	@Override
	public final Rectangle bounds() {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (RecTree recTree : content) {
			Rectangle r = recTree.bounds();
			if (r.x() < minX) {
				minX = r.x();
			}
			if (r.y() < minY) {
				minY = r.y();
			}
			if (r.x() + r.width() - 1 > maxX) {
				maxX = r.x() + r.width() - 1;
			}
			if (r.y() + r.height() - 1 > maxY) {
				maxY = r.y() + r.height() - 1;
			}
		}
		return new BasicRectangle(
			minX,
			minY,
			maxX - minX + 1,
			maxY - minY + 1
		);
	}

	@Override
	public RecTree part(String name) {
		if (!named.containsKey(name)) {
			throw new IllegalArgumentException(
				"Trying to get a part named \"" + name + "\" when there is no such part"
			);
		}
		return named.get(name);
	}

	@Override
	public RecTree nestedPart(String name) {
		for (NamedRecTree innerPart : parts()) {
			RecTree foundPart = nestedPartWithoutError(innerPart, name);
			if (foundPart != null) {
				return foundPart;
			}
		}
		throw new IllegalArgumentException(
			"Trying to get a part named \"" + name + "\" when there is no such part"
		);
	}

	private static RecTree nestedPartWithoutError(NamedRecTree part, String name) {
		if (part.hasName(name)) {
			return part;
		} else {
			for (NamedRecTree innerPart : part.parts()) {
				RecTree foundPart = nestedPartWithoutError(innerPart, name);
				if (foundPart != null) {
					return foundPart;
				}
			}
		}
		return null;
	}
}
