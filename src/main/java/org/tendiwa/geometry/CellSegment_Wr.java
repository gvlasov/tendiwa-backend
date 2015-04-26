package org.tendiwa.geometry;

import org.tendiwa.core.meta.Cell;

import java.util.Iterator;
import java.util.List;

public abstract class CellSegment_Wr implements CellSegment {
	private final CellSegment segment;

	protected CellSegment_Wr(CellSegment segment) {
		this.segment = segment;
	}

	@Override
	public Cell start() {
		return segment.start();
	}

	@Override
	public Cell end() {
		return segment.end();
	}

	@Override
	public Iterator<Cell> iterator() {
		return segment.iterator();
	}

	@Override
	public double length() {
		return segment.length();
	}

	@Override
	public List<Cell> asList() {
		return segment.asList();
	}
}
