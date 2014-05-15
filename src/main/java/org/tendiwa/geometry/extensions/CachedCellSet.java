package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.CellSet;
import org.tendiwa.geometry.Rectangle;

import static java.util.Objects.requireNonNull;

/**
 * A CellSet that holds a finite number of {@link org.tendiwa.geometry.Cell}s.
 * <p>
 * Stores buffer border for an area #width #height cells large,
 * delegating to #source if buffer border status for a cell has  not  yet  been  computed.
 */
@SuppressWarnings("unused")
public class CachedCellSet implements BoundedCellSet {
    private static final short NOT_CACHED = 0;
    private static final short IN_BUFFER = 1;
    private static final short NOT_IN_BUFFER = 2;
    private final CellSet source;

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    private Rectangle bounds;
    private final short[][] cache;

    public CachedCellSet(CellSet source, Rectangle bounds) {
        this.source = requireNonNull(source);
        this.bounds = requireNonNull(bounds);
        this.cache = new short[bounds.getWidth()][bounds.getHeight()];
    }

    @Override
    /**
     * Retrieves cached cell status, or computes it and caches if it has not yet been cached.
     */
    public boolean contains(int x, int y) {
        if (x < bounds.x || x > bounds.getMaxX()) {
            throw new IllegalArgumentException(
                    "x must be in [" + bounds.x + ";" + bounds.getMaxX() + "] (x == " + x + ")"
            );
        }
        if (y < bounds.y || y > bounds.getMaxY()) {
            throw new IllegalArgumentException(
                    "y must be in [" + bounds.y + ";" + bounds.getMaxY() + "] (y == " + y + ")");
        }
        if (cache[x - bounds.x][y - bounds.y] == NOT_CACHED) {
            if (source.contains(x, y)) {
                cache[x - bounds.x][y - bounds.y] = IN_BUFFER;
//                cellList.add(new Cell(x, y));
            } else {
                cache[x - bounds.x][y - bounds.y] = NOT_IN_BUFFER;
            }
        }
        assert cache[x - bounds.x][y - bounds.y] == IN_BUFFER || cache[x - bounds.x][y - bounds.y] == NOT_IN_BUFFER;
        assert cache[x - bounds.x][y - bounds.y] != NOT_CACHED;
        return cache[x - bounds.x][y - bounds.y] == IN_BUFFER;
    }

    /**
     * Test each cell in {@link #getBounds()} and saves it in the cache.
     */
    public CachedCellSet computeAll() {
        for (int i = 0; i < bounds.getWidth(); i++) {
            for (int j = 0; j < bounds.getHeight(); j++) {
                contains(i + bounds.x, j + bounds.y);
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "CachedCellSet{" +
                "bounds=" + bounds +
                '}';
    }
}
