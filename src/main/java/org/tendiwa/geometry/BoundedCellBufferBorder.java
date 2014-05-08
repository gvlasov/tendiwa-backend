package org.tendiwa.geometry;

public interface BoundedCellBufferBorder extends CellBufferBorder {
    /**
     * Returns a rectangle in which all cells of this CellBufferBorder lie.
     * <p>
     * Note that the bound in not necessarily the least
     * rectangular hull of all computed cells.
     *
     * @return Rectangular bounds of this CellBufferBorder.
     */
    public Rectangle getBounds();
}
