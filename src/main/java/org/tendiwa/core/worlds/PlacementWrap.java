package org.tendiwa.core.worlds;

import org.tendiwa.core.FloorType;
import org.tendiwa.geometry.*;

import java.util.Random;
import java.util.function.Function;

import static org.tendiwa.groovy.Registry.floorTypes;

public final class PlacementWrap implements Placement<FloorType> {

	private final BoundedCellSet cells;
	private final Function<BasicCell, FloorType> function;

	PlacementWrap(
		BoundedCellSet cells,
		Function<BasicCell, FloorType> function
	) {
		this.cells = cells;
		this.function = function;
		Placement<FloorType> p = new PlacementWrap(
			new BasicBoundedCells(
				(x, y) -> (x + y) % 7 == 0,
				DSL.rectangle(80, 90)
			),
			new RandomContent<>(
				new Random(123),
				(c, r) -> r.nextBoolean() ? floorTypes.get("grass") : floorTypes.get("stone")
			)
		);
	}

	@Override
	public FloorType contentAt(BasicCell cell) {
		return function.apply(cell);
	}

	@Override
	public Rectangle getBounds() {
		return cells.getBounds();
	}

	@Override
	public boolean contains(int x, int y) {
		return cells.contains(x, y);
	}
}
