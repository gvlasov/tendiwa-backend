package org.tendiwa.terrain;

public interface CellParamsFactory<T extends CellParams> {
	public T create(int x, int y);
}
