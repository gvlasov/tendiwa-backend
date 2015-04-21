package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a most basic placeable collection of non-overlapping rectangles. Unlike {@link RectangleSystem},
 * RectangleSequence doesn't maintain neighborship and outerness of rectangles.
 */
final class BasicRectangleSequence implements RectSet {
	/**
	 * RectangleAreas that are parts of this RectangleSystem.
	 */
	private final ImmutableCollection<NamedRectSet> content;
	private final Map<String, NamedRectSet> named;

	/**
	 * Creates an empty RectangleSequence.
	 */
	BasicRectangleSequence(ImmutableCollection<NamedRectSet> content) {
		this.content = content;
		this.named = createNameMap(content);
	}

	private Map<String, NamedRectSet> createNameMap(ImmutableCollection<NamedRectSet> content) {
		Map<String, NamedRectSet> map = new LinkedHashMap<>(content.size());
		content.forEach(
			r -> r.name().ifPresent(
				name -> map.put(name, r)
			)
		);
		return map;
	}

	@Override
	public ImmutableCollection<NamedRectSet> parts() {
		return content;
	}

	@Override
	public final Rectangle bounds() {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (RectSet rectSet : content) {
			Rectangle r = rectSet.bounds();
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
	public RectSet part(String name) {
		if (!named.containsKey(name)) {
			throw new IllegalArgumentException(
				"Trying to get a part named \"" + name + "\" when there is no such part"
			);
		}
		return named.get(name);
	}

	@Override
	public RectSet nestedPart(String name) {
		for (NamedRectSet innerPart : parts()) {
			RectSet foundPart = nestedPartWithoutError(innerPart, name);
			if (foundPart != null) {
				return foundPart;
			}
		}
		throw new IllegalArgumentException(
			"Trying to get a part named \"" + name + "\" when there is no such part"
		);
	}

	private static RectSet nestedPartWithoutError(NamedRectSet part, String name) {
		if (part.hasName(name)) {
			return part;
		} else {
			for (NamedRectSet innerPart : part.parts()) {
				RectSet foundPart = nestedPartWithoutError(innerPart, name);
				if (foundPart != null) {
					return foundPart;
				}
			}
		}
		return null;
	}
}
