package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

public interface BoundedCellBufferBorder extends CellBufferBorder {
    /**
     * Returns a rectangle in which all cells of this CellBufferBorder lie.
     * Note that the bound in not necessarily the least
     * rectangular hull of all computed cells.
     *
     * @return Rectangular bounds of this CellBufferBorder.
     */
    public Rectangle getBounds();

    /**
     * Creates a new ImmutableList containing all of cells within {@link #getBounds()} that are
     * buffer border cells.
     *
     * @return A new ImmutableList
     */
    public default ImmutableList<Cell> cellList() {
        ImmutableList.Builder<Cell> builder = ImmutableList.builder();
        for (int i = 0; i < getBounds().getWidth(); i++) {
            for (int j = 0; j < getBounds().getHeight(); j++) {
                if (isBufferBorder(i + getBounds().x, j + getBounds().y)) {
                    builder.add(new Cell(i + getBounds().x, j + getBounds().y));
                }
            }
        }
        return builder.build();
    }
}
