package org.tendiwa.geometry.extensions;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.CellBufferBorder;
import org.tendiwa.geometry.Rectangle;

import java.util.LinkedList;

/**
 * Stores buffer border for an area #width #height cells large,
 * delegating to #source if buffer border status for a cell has  not  yet  been  computed.
 */
@SuppressWarnings("unused")
public class CachedCellBufferBorder implements CellBufferBorder {
    private static final short NOT_CACHED = 0;
    private static final short IN_BUFFER = 1;
    private static final short NOT_IN_BUFFER = 2;
    private final CellBufferBorder source;
    private Rectangle rectangle;
    private final short[][] cache;
    private LinkedList<Cell> cellList = new LinkedList<>();

    public CachedCellBufferBorder(CellBufferBorder source, Rectangle rectangle) {

        this.source = source;
        this.rectangle = rectangle;
        this.cache = new short[rectangle.getWidth()][rectangle.getHeight()];
    }

    @Override
    /**
     * Retrieves cached cell status, or computes it and caches if it has not yet been cached.
     */
    public boolean isBufferBorder(int x, int y) {
        if (x < rectangle.x || x > rectangle.getMaxX()) {
            throw new IllegalArgumentException(
                    "x must be in [" + rectangle.x + ";" + rectangle.getMaxX() + "] (x == " + x + ")"
            );
        }
        if (y < rectangle.y || y > rectangle.getMaxY()) {
            throw new IllegalArgumentException(
                    "y must be in [" + rectangle.y + ";" + rectangle.getMaxY() + "] (y == " + y + ")");
        }
        if (cache[x - rectangle.x][y - rectangle.y] == NOT_CACHED) {
            if (source.isBufferBorder(x, y)) {
                cache[x - rectangle.x][y - rectangle.y] = IN_BUFFER;
                cellList.add(new Cell(x, y));
            } else {
                cache[x - rectangle.x][y - rectangle.y] = NOT_IN_BUFFER;
            }
        }
        assert cache[x - rectangle.x][y - rectangle.y] == IN_BUFFER || cache[x - rectangle.x][y - rectangle.y] == NOT_IN_BUFFER;
        assert cache[x - rectangle.x][y - rectangle.y] != NOT_CACHED;
        return cache[x - rectangle.x][y - rectangle.y] == IN_BUFFER;
    }

    /**
     * Computes and caches all cells' statuses.
     */
    public void computeAll() {
        for (int i = 0; i < rectangle.getWidth(); i++) {
            for (int j = 0; j < rectangle.getHeight(); j++) {
                isBufferBorder(i, j);
            }
        }
    }

    public ImmutableList<Cell> cellList() {
        return ImmutableList.copyOf(cellList);
    }
}
