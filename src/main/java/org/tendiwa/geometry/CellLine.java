package org.tendiwa.geometry;

import com.google.common.collect.Iterators;

import java.util.Iterator;

public class CellLine implements Iterable<Cell> {
    private final Cell[] cells;

    public CellLine(Cell start, Cell end) {
        this.cells = vector(start.x, start.y, end.x, end.y);
    }

    public static Cell[] vector(int startX, int startY, int endX, int endY) {
        int l = Math.round(Math.max(Math.abs(endX - startX),
                Math.abs(endY - startY)));
        float x[] = new float[l + 2];
        float y[] = new float[l + 2];
        Cell result[] = new Cell[l + 1];

        x[0] = startX;
        y[0] = startY;

        if (startX == endX && startY == endY) {
            result = new Cell[1];
            result[0] = new Cell(startX, startY);
            return result;
        }
        float dx = (endX - startX) / (float) l;
        float dy = (endY - startY) / (float) l;
        for (int i = 1; i <= l; i++) {
            x[i] = x[i - 1] + dx;
            y[i] = y[i - 1] + dy;
        }
        x[l + 1] = endX;
        y[l + 1] = endY;

        for (int i = 0; i <= l; i++) {
            result[i] = new Cell(Math.round(x[i]), Math.round(y[i]));
        }
        return result;
    }

    @Override
    public Iterator<Cell> iterator() {
        return Iterators.forArray(cells);
    }
}
